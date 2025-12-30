import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> taskMap = new HashMap<>();// хранение Task
    private final Map<Integer, Epic> epicMap = new HashMap<>();// хранение Epic
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();// хран

    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTask = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void createEpic(Epic epic) {
        epicMap.put(epic.getIdTask(), epic);
        prioritizedTask.add(epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epicMap.get(subtask.getEpicIdTask());
        if (epic == null) {
            System.out.println("Эпик этого сабтаска не найден.");
            return;
        }
        epic.addSubtask(subtask.getIdTask());
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
        for (Integer subId : epic.getSubtaskSet()) {
            Subtask subtask = subtaskMap.get(subId);
            {
                historyManager.add(subtask);
            }
        }
        return subtasks;
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public void recalcEpicStatus(Epic epic) {
        if (epic.getSubtaskSet().isEmpty()) {
            epic.setStatus(Task.Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : epic.getSubtaskSet()) {
            Subtask subtask = subtaskMap.get(subId);
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
            for (Integer subtaskIds : epic.getSubtaskSet()) {
                historyManager.remove(subtaskIds);
                prioritizedTask.remove(subtaskIds);
                subtaskMap.remove(subtaskIds);
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
            }
        }
        subtaskMap.clear();
    }

    @Override
    public void removeAllTasks() {
        for (Task task : historyManager.getHistory()) {
            if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                historyManager.remove(task.getIdTask());
            }
        }
        taskMap.clear();

    }

    @Override
    public void removeAllTypesOfTasks() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
        historyManager.getHistory().clear();
        prioritizedTask.clear();

    }

    @Override
    public void removeEpicById(int id) {
        if (epicMap.get(id) != null) {
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
            epic.getSubtaskSet().remove(subtaskToRemove.getIdTask());
            recalcEpicStatus(epic);
        }
        subtaskMap.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeTaskById(int id) {
        Task taskToRemove = taskMap.get(id);
        if (taskToRemove == null) {
            System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
        }

        taskMap.remove(id); // Удаляем саму задачу/эпик
        historyManager.remove(id);
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {

        Epic oldEpic = epicMap.get(oldId);

        // сохраняем связь с подзадачами

        for (Integer i : oldEpic.getSubtaskSet()) {
            newEpic.addSubtask(i);
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
        Epic epic = epicMap.get(newSubtask.getEpicIdTask());
        if (epic != null) {
            recalcEpicStatus(epic);
        }
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        taskMap.replace(oldId, newTask);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    // -------------------- Вспомогательное --------------------

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" + "historyManager=" + historyManager + '}';
    }
}
