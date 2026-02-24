import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
  private static final LocalDateTime NO_TIME = LocalDateTime.of(1, 1, 1, 1, 1);
  private static final String timeOverLapError =
      "Новая задача пересекается по времени с уже существующими задачами";
  private final Map<Integer, Task> taskMapAllTypes = new HashMap<>();
  private final HistoryManager historyManager;
  private final Set<Task> prioritizedTasks =
      new TreeSet<>(
          Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));

  public InMemoryTaskManager(HistoryManager historyManager) {
    this.historyManager = historyManager;
  }

  private void addTaskByPriority(Task task) {
    if (task.getStartTime() != null && !task.getStartTime().equals(NO_TIME)) {
      prioritizedTasks.add(task);
    }
  }

  private void createEpic(Epic epic) {
    taskMapAllTypes.put(epic.getIdTask(), epic);
  }

  private void createSubtask(Subtask subtask) {
    if (!isNoOverLap(subtask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    Epic epic = (Epic) taskMapAllTypes.get(subtask.getEpicIdTask());
    if (epic == null) {
      throw new TaskManagerRuntimeException(
          "Эпик с ID " + subtask.getEpicIdTask() + " для подзадачи не найден.");
    }
    epic.addSubtask(subtask);
    recalcEpicStatus(epic);
    taskMapAllTypes.put(subtask.getIdTask(), subtask);
    addTaskByPriority(subtask);
  }

  @Override
  public void createTask(Task task) {
    if (!isNoOverLap(task)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    switch (task.getType()) {
      case TASK -> {
        taskMapAllTypes.put(task.getIdTask(), task);
        addTaskByPriority(task);
      }
      case EPIC -> createEpic((Epic) task);
      case SUBTASK -> createSubtask((Subtask) task);
      default -> throw new IllegalArgumentException("Неверный тип задачи");
    }
  }

  @Override
  public List<Epic> getEpicList() {
    return taskMapAllTypes.values().stream()
        .filter(task -> task.getType() == TypesOfTask.EPIC)
        .map(task -> (Epic) task)
        .toList();
  }

  @Override
  public List<Task> getHistory() {
    return historyManager.getHistory();
  }

  @Override
  public List<Task> getListOfAllTasks() {
    return taskMapAllTypes.values().stream().toList();
  }

  @Override
  public List<Task> getPrioritizedTasks() {
    return List.copyOf(prioritizedTasks);
  }

  @Override
  public List<Subtask> getSubtaskList() {
    return taskMapAllTypes.values().stream()
        .filter(task -> task.getType() == TypesOfTask.SUBTASK)
        .map(task -> (Subtask) task)
        .toList();
  }

  @Override
  public List<Subtask> getSubtasksOfEpic(int epicId) {
    Epic epic = (Epic) taskMapAllTypes.get(epicId);
    if (epic == null || epic.getType() != TypesOfTask.EPIC) {
      throw new TaskManagerRuntimeException(
          "Эпик с ID "
              + epicId
              + " не найден или имеет неверный тип. Получение подзадач не выполнено.");
    }
    List<Subtask> subtasks = new ArrayList<>();
    for (Subtask subtask : epic.getSubtaskSet()) {
      subtasks.add(subtask);
    }
    return subtasks;
  }

  @Override
  public Task getTaskById(int id) {
    if (!taskMapAllTypes.containsKey(id)) {
      throw new TaskManagerRuntimeException("Задача с ID " + id + " не найдена.");
    }
    historyManager.add(taskMapAllTypes.get(id));
    return taskMapAllTypes.get(id);
  }

  @Override
  public List<Task> getTaskList() {
    return taskMapAllTypes.values().stream().toList();
  }

  public Map<Integer, Task> getTaskMap() {
    return taskMapAllTypes;
  }

    private boolean isNoOverLap(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start.isEqual(NO_TIME)) {
            return true; // Нет времени — нет пересечения
        }

        return prioritizedTasks.isEmpty()
            || prioritizedTasks.stream()
            .allMatch(taskPrior ->
                taskPrior.getEndTime().isBefore(start) ||
                    taskPrior.getEndTime().isEqual(start) ||
                    taskPrior.getStartTime().isAfter(end) ||
                    taskPrior.getStartTime().isEqual(end));
    }

  private void recalcEpicStatus(Epic epic) {
    if (epic.getSubtaskSet().isEmpty()) {
      epic.setStatus(Task.Status.NEW);
      return;
    }

    boolean allNew = true;
    boolean allDone = true;

    for (Subtask sub : epic.getSubtaskSet()) {
      Subtask subtask = (Subtask) taskMapAllTypes.get(sub.getIdTask());
      if (subtask != null) {
        Task.Status st = subtask.getStatus();
        if (st != Task.Status.NEW) {
          allNew = false;
        }
        if (st != Task.Status.DONE) {
          allDone = false;
        }
      }
    }

    if (allNew) {
      epic.setStatus(Task.Status.NEW);
    } else if (allDone) {
      epic.setStatus(Task.Status.DONE);
    } else {
      epic.setStatus(Task.Status.IN_PROGRESS);
    }
  }

  @Override
  public void removeAllEpics() {
    getEpicList().stream().map(Task::getIdTask).forEach(this::removeEpicById);
  }

  @Override
  public void removeAllOrdinaryTasks() {
    List<Integer> taskIds =
        taskMapAllTypes.values().stream()
            .filter(task -> task.getType() == TypesOfTask.TASK)
            .map(Task::getIdTask)
            .toList();

    taskIds.forEach(
        id -> {
          Task taskToRemove = taskMapAllTypes.get(id);
          removeTaskFromCollections(taskToRemove);
        });
  }

  @Override
  public void removeAllSubtasks() {
    List<Integer> subtaskIds =
        taskMapAllTypes.values().stream()
            .filter(task -> task.getType() == TypesOfTask.SUBTASK)
            .map(Task::getIdTask)
            .toList();
    subtaskIds.forEach(this::removeSubtaskById);
  }

  @Override
  public void removeAllTypesOfTasks() {
    removeAllOrdinaryTasks();
    removeAllSubtasks();
    removeAllEpics();
    prioritizedTasks.clear();
  }

  private void removeEpicById(int id) {
    Task epic = taskMapAllTypes.get(id);
    if (epic == null || epic.getType() != TypesOfTask.EPIC) {
      throw new TaskManagerRuntimeException(
          "Эпик с ID " + id + " не найден или имеет неверный тип. Удаление не выполнено.");
    }

    Epic epicToRemove = (Epic) epic;

    List<Subtask> subtaskListForRemove = new ArrayList<>(epicToRemove.getSubtaskSet());
    for (Subtask subtask : subtaskListForRemove) {
      removeSubtaskById(subtask.getIdTask());
    }

    removeTaskFromCollections(epicToRemove);
  }

  private void removeSubtaskById(int id) {
    Subtask subtaskToRemove = (Subtask) taskMapAllTypes.get(id);
    if (subtaskToRemove == null || subtaskToRemove.getType() != TypesOfTask.SUBTASK) {
      throw new TaskManagerRuntimeException(
          "Подзадача с ID " + id + " не найдена или имеет неверный тип. Удаление не выполнено.");
    }
    Epic epic = (Epic) taskMapAllTypes.get(subtaskToRemove.getEpicIdTask());
    if (epic != null) {
      epic.deleteSubtaskFromSubtasksSet(subtaskToRemove);
      recalcEpicStatus(epic);
      epic.calculateEndTime();
    }
    removeTaskFromCollections(subtaskToRemove);
  }

  @Override
  public void removeTaskById(int id) {
    Task taskToRemove = taskMapAllTypes.get(id);
    if (taskToRemove == null) {
      throw new TaskManagerRuntimeException(
          "Задача с ID " + id + " не найдена. Удаление не выполнено.");
    }
    switch (taskToRemove.getType()) {
      case TASK -> {
        removeTaskFromCollections(taskToRemove);
      }
      case EPIC -> removeEpicById(taskToRemove.getIdTask());
      case SUBTASK -> removeSubtaskById(taskToRemove.getIdTask());
      default -> throw new IllegalArgumentException("Неверный тип задачи");
    }
  }

  private void removeTaskFromCollections(Task task) {
    if (taskMapAllTypes.get(task.getIdTask()) != null) {
      taskMapAllTypes.remove(task.getIdTask());
      historyManager.remove(task.getIdTask());
      prioritizedTasks.remove(task);
    } else
      throw new TaskManagerRuntimeException(
          "Задача с ID " + task.getIdTask() + " не найдена. Удаление не выполнено.");
  }

  @Override
  public String toString() {
    return "InMemoryTaskManager{" + "historyManager=" + historyManager + '}';
  }

  @Override
  public void updateEpic(Epic newEpic, int oldId) {
      Task taskToUpdate = taskMapAllTypes.get(oldId);
      if (taskToUpdate == null || taskToUpdate.getType() != TypesOfTask.EPIC) {
          throw new TaskManagerRuntimeException(
              "Эпик с ID " + oldId + " не найден или имеет неверный тип. Обновление не выполнено.");
      }

      Epic oldEpic = (Epic) taskToUpdate;

      // Переносим все подзадачи в новый эпик
      for (Subtask subtask : oldEpic.getSubtaskSet()) {
          newEpic.addSubtask(subtask);
      }

      // Проверяем пересечение нового эпика с существующими задачами
      if (!isNoOverLap(newEpic)) {
          throw new TaskTimeOverlapException(timeOverLapError);
      }

      // Обновляем эпик в коллекции
      taskMapAllTypes.replace(oldId, newEpic);

      // Пересчитываем статус
      recalcEpicStatus(newEpic);
  }

  @Override
  public void updateSubtask(Subtask newSubtask, int oldId) {
    // Проверка существования подзадачи
    Task taskToUpdate = taskMapAllTypes.get(oldId);
    if (taskToUpdate == null || taskToUpdate.getType() != TypesOfTask.SUBTASK) {
      throw new TaskManagerRuntimeException(
          "Подзадача с ID " + oldId + " не найдена или имеет неверный тип.");
    }
    Subtask oldSubtask = (Subtask) taskToUpdate;
    // Удаляем подзадачу из старого эпика
    try {
      Epic oldEpic = (Epic) taskMapAllTypes.get(oldSubtask.getEpicIdTask());
      if (oldEpic != null) {
        oldEpic.deleteSubtaskFromSubtasksSet(oldSubtask);
      }
    } catch (ClassCastException e) {
      throw new TaskManagerRuntimeException("Ошибка: старый эпик подзадачи имеет неверный тип.", e);
    }

    // Проверка пересечения по времени
    if (!isNoOverLap(newSubtask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }

    // Обновляем приоритезированные задачи
    prioritizedTasks.remove(oldSubtask);
    taskMapAllTypes.replace(oldId, newSubtask);
    addTaskByPriority(newSubtask);

    // Добавляем подзадачу в новый эпик и пересчитываем статусы
    try {
      Epic newEpic = (Epic) taskMapAllTypes.get(newSubtask.getEpicIdTask());
      if (newEpic != null) {
        newEpic.addSubtask(newSubtask);
        recalcEpicStatus(newEpic);
        newEpic.calculateEndTime();
      } else {
        throw new TaskManagerRuntimeException(
            "Эпик с ID " + newSubtask.getEpicIdTask() + " для подзадачи не найден.");
      }
    } catch (ClassCastException e) {
      throw new TaskManagerRuntimeException("Ошибка: новый эпик подзадачи имеет неверный тип.", e);
    }
  }

  @Override
  public void updateTask(Task newTask, int oldId) {
    if (!taskMapAllTypes.containsKey(oldId)) {
      throw new TaskManagerRuntimeException(
          "Задача с ID " + oldId + " не найдена. Обновление не выполнено.");
    }
    if (newTask.getType() != TypesOfTask.TASK) {
      throw new TaskManagerRuntimeException("Неверный тип задачи для обновления. Ожидался TASK.");
    }
    if (!isNoOverLap(newTask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    Task oldTask = taskMapAllTypes.get(oldId);
    prioritizedTasks.remove(oldTask);
    taskMapAllTypes.replace(oldId, newTask);
    addTaskByPriority(newTask);
  }
}
