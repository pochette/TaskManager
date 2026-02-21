import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
  private static final LocalDateTime NO_TIME = LocalDateTime.of(1, 1, 1, 1, 1);
  private static final String timeOverLapError =
      "Новая задача пересекается по времени с уже существующими задачами";
  // хранение всех типов задач в одном Map для удобства загрузки из файла
  private final Map<Integer, Task> taskMapAllTypes = new HashMap<>();
  private final HistoryManager historyManager;
  private final Set<Task> prioritizedTasks =
      new TreeSet<>(
          Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));

  public InMemoryTaskManager(HistoryManager historyManager) {
    this.historyManager = historyManager;
  }

  protected void addTaskByPriority(Task task) {
    if (task.getStartTime() != null && !task.getStartTime().equals(NO_TIME)) {
      prioritizedTasks.add(task);
    }
  }

  protected void createEpic(Epic epic) {
      taskMapAllTypes.put(epic.getIdTask(), epic);
    taskMapAllTypes.put(epic.getIdTask(), epic);
  }

  protected void createSubtask(Subtask subtask) {
    if (!isNoOverLap(subtask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    Epic epic = (Epic) taskMapAllTypes.get(subtask.getEpicIdTask());
    if (epic == null) {
      System.out.println("Эпик этого сабтаска не найден.");
      return;
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
        addTaskByPriority(task);
        taskMapAllTypes.put(task.getIdTask(), task);

      }
      case EPIC -> createEpic((Epic) task);
      case SUBTASK -> createSubtask((Subtask) task);
      default -> throw new IllegalArgumentException("Неверный тип задачи");
    }
  }

  @Override
  public Epic getEpicById(int id) {
    historyManager.add(taskMapAllTypes.get(id));
    return (Epic) taskMapAllTypes.get(id);
  }

  @Override
  public List<Epic> getEpicList() {
    return new ArrayList<>(taskMapAllTypes.values().stream()
        .filter(task -> task.getType() == TypesOfTask.EPIC)
        .map(task -> (Epic) task)
        .toList());
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

  protected Subtask getSubtaskById(int id) {
    historyManager.add(taskMapAllTypes.get(id));
    return (Subtask) taskMapAllTypes.get(id);
  }

  @Override
  public List<Subtask> getSubtaskList() {
    return taskMapAllTypes.values().stream().
      .
        toList();
  }

  @Override
  public List<Subtask> getSubtasksOfEpic(int epicId) {
    if (!epicMap.containsKey(epicId)) {
      System.out.println("Эпик с ID " + epicId + " не найден. Подзадачи не найдены.");
      return Collections.emptyList();
    }
    Epic epic = epicMap.get(epicId);
    List<Subtask> subtasks = new ArrayList<>();
    for (Subtask subtask : epic.getSubtaskSet()) {
      historyManager.add(subtask);
      subtasks.add(subtask);
    }
    return subtasks;
  }

  @Override
  public Task getTaskById(int id) {
    switch (taskMapAllTypes.get(id).getType()) {
      case TASK -> {
        historyManager.add(taskMap.get(id));
      }
      case EPIC -> {
        historyManager.add(epicMap.get(id));
      }
      case SUBTASK -> {
        historyManager.add(subtaskMap.get(id));
      }
      default -> throw new IllegalArgumentException("Неверный тип задачи");
    }
    historyManager.add(taskMapAllTypes.get(id));
    return taskMapAllTypes.get(id);
  }

  @Override
  public List<Task> getTaskList() {
    return taskMap.values().stream().toList();
  }

  public Map<Integer, Task> getTaskMap() {
    return taskMap;
  }

  private boolean isNoOverLap(
      Task task) { // Проверка на пересечение по времени задачи и уже имеющихся приоритезированных
    // задач
    LocalDateTime start = task.getStartTime();
    LocalDateTime end = task.getEndTime();
    if (start.isEqual(NO_TIME))
      return true; // Задачи без времени не попадают в проверку и не будут учтены при приоритезации
    // Для каждой приоритезированной задачи проверяем попадает ли новая задача во временные рамки
    if (!prioritizedTasks.isEmpty()) {
      return prioritizedTasks.stream()
          .allMatch(
              taskPrior ->
                  (taskPrior.getStartTime().isEqual(start) && taskPrior.getEndTime().isEqual(end))
                      || taskPrior.getStartTime().isAfter(end)
                      || taskPrior.getStartTime().isEqual(end)
                      || taskPrior.getEndTime().isBefore(start)
                      || taskPrior.getEndTime().isEqual(start));
    } else {
      return true;
    }
  }

  protected void recalcEpicStatus(Epic epic) {
    if (epic.getSubtaskSet().isEmpty()) {
      epic.setStatus(Task.Status.NEW);
      return;
    }

    boolean allNew = true;
    boolean allDone = true;

    for (Subtask sub : epic.getSubtaskSet()) {
      Subtask subtask = subtaskMap.get(sub.getIdTask());
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
    for (Integer i : epicMap.keySet()) {
      Epic epic = epicMap.get(i);
      for (Subtask subtask : epic.getSubtaskSet()) {
        historyManager.remove(subtask.getIdTask());
        prioritizedTasks.remove(subtask);
        subtaskMap.remove(subtask.getIdTask());
      }
      historyManager.remove(i);
    }
    epicMap.clear();
  }

  @Override
  public void removeAllOrdinaryTasks() {
    for (Task task : historyManager.getHistory()) {
      if (!(task instanceof Epic) && !(task instanceof Subtask)) {
        historyManager.remove(task.getIdTask());
        prioritizedTasks.remove(task);
      }
    }
    taskMap.clear();
  }

  @Override
  public void removeAllSubtasks() {

    List<Task> historyList = historyManager.getHistory();
    for (Task task : historyList) {
      if (task instanceof Subtask subtask) {
        historyManager.remove(subtask.getIdTask());
        prioritizedTasks.remove(subtask);
      }
    }
    subtaskMap.clear();
  }

  @Override
  public void removeAllTypesOfTasks() {
    removeAllOrdinaryTasks();
    removeAllSubtasks();
    removeAllEpics();
    prioritizedTasks.clear();
  }

  protected void removeEpicById(int id) {
    if (epicMap.get(id) != null) {
      Iterator<Subtask> iterator = epicMap.get(id).getSubtaskSet().iterator();
      while (iterator.hasNext()) {
        Subtask subtask = iterator.next();
        if (subtaskMap.get(subtask.getIdTask()) != null) {
          prioritizedTasks.remove(subtaskMap.get(subtask.getIdTask()));
          historyManager.remove(subtask.getIdTask());
          subtaskMap.remove(subtask.getIdTask());
        }
      }
      historyManager.remove(id);
      epicMap.get(id).deleteAllSubtasks();
      epicMap.remove(id);
    }
  }

  protected void removeSubtaskById(int id) {
    Subtask subtaskToRemove = subtaskMap.get(id);
    if (subtaskToRemove == null) {
      System.out.println("Подзадача с ID " + id + " не найдена. Удаление не выполнено.");
      return;
    }
    Epic epic = epicMap.get(subtaskToRemove.getEpicIdTask());
    if (epic != null) {
      epic.deleteSubtaskFromSubtasksSet(subtaskToRemove);
      recalcEpicStatus(epic);
      epic.calculateEndTime();
    }
    prioritizedTasks.remove(subtaskToRemove);
    historyManager.remove(id);
    subtaskMap.remove(id);
  }

  @Override
  public void removeTaskById(int id) {
    Task taskToRemove = taskMap.get(id);
    if (taskToRemove == null) {
      System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
      return;
    }
    switch (taskToRemove.getType()) {
      case TASK -> {
        prioritizedTasks.remove(taskToRemove);
        taskMap.remove(id);
        historyManager.remove(id);
      }
      case EPIC -> removeEpicById(taskToRemove.getIdTask());
      case SUBTASK -> removeSubtaskById(taskToRemove.getIdTask());
      default -> throw new IllegalArgumentException("Неверный тип задачи");
    }
  }

  @Override
  public String toString() {
    return "InMemoryTaskManager{" + "historyManager=" + historyManager + '}';
  }

  private void updateEpic(Epic newEpic, int oldId) {
    Epic oldEpic = epicMap.get(oldId);
    // сохраняем связь с подзадачами
    for (Subtask subtask : oldEpic.getSubtaskSet()) {
      newEpic.addSubtask(subtask);
    }
    // статус пересчитывается только по сабтаскам
    recalcEpicStatus(newEpic);
    epicMap.replace(oldId, newEpic);
  }

  protected void updateSubtask(Subtask newSubtask, int oldId) {
    Subtask oldSubtask = subtaskMap.get(oldId);
    if (oldSubtask == null) {
      System.out.println("Подзадача с ID " + oldId + " не найдена. Обновление не выполнено.");
      return;
    }
    if (!isNoOverLap(newSubtask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    prioritizedTasks.remove(oldSubtask);
    subtaskMap.replace(oldId, newSubtask);
    addTaskByPriority(newSubtask);
    Epic epic = epicMap.get(newSubtask.getEpicIdTask());
    if (epic != null) {
      recalcEpicStatus(epic);
      epic.calculateEndTime();
    }
  }

  @Override
  public void updateTask(Task newTask, int oldId) {
    if (!taskMap.containsKey(oldId)) {
      System.out.println("Задача с ID " + oldId + " не найдена. Обновление не выполнено.");
      return;
    }
    if (!isNoOverLap(newTask)) {
      throw new TaskTimeOverlapException(timeOverLapError);
    }
    Task oldTask = taskMap.get(oldId);
    prioritizedTasks.remove(oldTask);
    taskMap.replace(oldId, newTask);
    addTaskByPriority(newTask);
  }
}
