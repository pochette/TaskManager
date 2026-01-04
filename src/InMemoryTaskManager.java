import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();// хранение Task
    private final Map<Integer, Epic> epicMap = new HashMap<>();// хранение Epic
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();// хран

    private final HistoryManager historyManager;

    private final Set<Task> prioritizedTask = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(LocalDateTime::compareTo))
                    .thenComparing(Task::getIdTask)  // Добавляем вторичную сортировку по ID
    );

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void createEpic(Epic epic) {
        epicMap.put(epic.getIdTask(), epic);
        if(epic.getStartTime() != null) {
            prioritizedTask.add(epic);
        }
        prioritizedTask.add(epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicIdTask());
        if (epic == null) {
            System.out.println("Эпик этого сабтаска не найден.");
            return;
        }
        epic.addSubtask(subtask);
        recalcEpicStatus(epic);
        subtaskMap.put(subtask.getIdTask(), subtask);
        prioritizedTask.add(subtask);
    }

    @Override
    public void createTask(Task task) {
        taskMap.put(task.getIdTask(), task);
        prioritizedTask.add(task);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getListOfAllTasks() {
        List<Task> taskList = new ArrayList<>();
        taskList.addAll(taskMap.values());
        taskList.addAll(epicMap.values());
        taskList.addAll(subtaskMap.values());
        return taskList;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTask);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return subtaskMap.values().stream().toList();
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
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public List<Task> getTaskList() {
        return taskMap.values().stream().toList();
    }

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    public void recalcEpicStatus(Epic epic) {
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
                prioritizedTask.remove(subtask);
                subtaskMap.remove(subtask.getIdTask());
            }
            historyManager.remove(i);
        }
        epicMap.clear();
    }

    @Override
    public void removeAllSubtasks() {

        List<Task> historyList = historyManager.getHistory();
        for (Task task : historyList) {
            if (task instanceof Subtask) {
                historyManager.remove(task.getIdTask());
                prioritizedTask.remove(task);
            }
        }
        subtaskMap.clear();
    }

    @Override
    public void removeAllTasks() {
        for (Task task : historyManager.getHistory()) {
            if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                historyManager.remove(task.getIdTask());
                prioritizedTask.remove(task);
            }
        }
        taskMap.clear();

    }

    @Override
    public void removeAllTypesOfTasks() {
        removeAllTasks();
        removeAllSubtasks();
        removeAllEpics();
        prioritizedTask.clear();
    }

    @Override
    public void removeEpicById(int id) {
        if (epicMap.get(id) != null) {
            Iterator<Subtask> iterator = epicMap.get(id).getSubtaskSet().iterator();
            while (iterator.hasNext()) {
                Subtask subtask = iterator.next();
                if (subtaskMap.get(subtask.getIdTask()) != null) {
                    prioritizedTask.remove(subtaskMap.get(subtask.getIdTask()));
                    historyManager.remove(subtask.getIdTask());
                    subtaskMap.remove(subtask.getIdTask());
                }
            }
            prioritizedTask.remove(epicMap.get(id));
            historyManager.remove(id);
            epicMap.get(id).deleteAllSubtasks();
            epicMap.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtaskToRemove = subtaskMap.get(id);
        if (subtaskToRemove == null) {
            System.out.println("Подзадача с ID " + id + " не найдена. Удаление не выполнено.");
            return;
        }
        Epic epic = epicMap.get(subtaskToRemove.getEpicIdTask());
        if (epic != null) {
            epic.deleteSubtaskFromSubtasksSet(subtaskToRemove);
            recalcEpicStatus(epic);
        }
        prioritizedTask.remove(subtaskToRemove);
        historyManager.remove(id);
        subtaskMap.remove(id);

    }

    @Override
    public void removeTaskById(int id) {
        Task taskToRemove = taskMap.get(id);
        if (taskToRemove == null) {
            System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
        }
        prioritizedTask.remove(taskToRemove);
        taskMap.remove(id); // Удаляем саму задачу/эпик
        historyManager.remove(id);
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" + "historyManager=" + historyManager + '}';
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {
        Epic oldEpic = epicMap.get(oldId);
        // сохраняем связь с подзадачами
        for (Subtask subtask : oldEpic.getSubtaskSet()) {
            newEpic.addSubtask(subtask);
        }
        // статус пересчитывается только по сабтаскам
        recalcEpicStatus(newEpic);
        epicMap.replace(oldId, newEpic);
}



    @Override
    public void updateSubtask(Subtask newSubtask, int oldId) {
        if (!subtaskMap.containsKey(oldId)) {
            System.out.println("Подзадача с ID " + oldId + " не найдена. Обновление не выполнено.");
            return;
        }
        subtaskMap.replace(oldId, newSubtask);
        prioritizedTask.remove(subtaskMap.get(oldId));
        prioritizedTask.add(newSubtask);
        Epic epic = epicMap.get(newSubtask.getEpicIdTask());
        if (epic != null) {
            recalcEpicStatus(epic);
            epic.calculateEndTime();
        }
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        taskMap.replace(oldId, newTask);
        prioritizedTask.remove(taskMap.get(oldId));
        prioritizedTask.add(newTask);
    }
}
