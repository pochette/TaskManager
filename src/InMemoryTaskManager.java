import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static final LocalDateTime NO_TIME = LocalDateTime.of(1, 1, 1, 1, 1);
    private static final String timeOverLapError = "Новая задача пересекается по времени с уже существующими задачами";
    private final Map<Integer, Task> taskMap = new HashMap<>();// хранение Task
    private final Map<Integer, Epic> epicMap = new HashMap<>();// хранение Epic
    private final Map<Integer, Subtask> subtaskMap = new HashMap<>();// хранение Subtask
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    protected void addTaskByPriority(Task task) {
        if (task.getStartTime() != null && !task.getStartTime().equals(NO_TIME)) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epicMap.put(epic.getIdTask(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) throws TaskTimeOverLapException {
        if (!isNoOverLap(subtask)) {
            throw new TaskTimeOverLapException(timeOverLapError);
        }
        Epic epic = epicMap.get(subtask.getEpicIdTask());
        if (epic == null) {
            System.out.println("Эпик этого сабтаска не найден.");
            return;
        }
        epic.addSubtask(subtask);
        recalcEpicStatus(epic);
        subtaskMap.put(subtask.getIdTask(), subtask);
        addTaskByPriority(subtask);
    }

    @Override
    public void createTask(Task task) {
        if (!isNoOverLap(task)) {
            throw new TaskTimeOverLapException(timeOverLapError);
        }
        taskMap.put(task.getIdTask(), task);
        addTaskByPriority(task);
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
        return List.copyOf(prioritizedTasks);
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

//    private boolean isNoOverLap(Task task) {
//        LocalDateTime start = task.getStartTime();
//        LocalDateTime end = task.getEndTime();
//        if (start.equals(NO_TIME)) return true;
//        return prioritizedTask.stream()
//                .allMatch(taskPrior -> (
//                        taskPrior.getStartTime().isEqual(start) &&
//                                taskPrior.getStartTime().isAfter(end) ||
//                                taskPrior.getStartTime().isEqual(end) ||
//                                taskPrior.getEndTime().isBefore(start) ||
//                                taskPrior.getEndTime().isEqual(start) ||
//                                taskPrior.getEndTime().isEqual(end)));
//    }

    private boolean isNoOverLap(Task task) { // Проверка на пересечение по времени задачи и уже имеющихся приоритезированных задач
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start.isEqual(NO_TIME))
            return true; // Задачи без времени не попадают в проверку и не будут учтены при приоритезации
        // Для каждой приоритезированной задачи проверяем попадает ли новая задача во временные рамки
        if (!prioritizedTasks.isEmpty()) {
            return prioritizedTasks.stream()
                    .allMatch(taskPrior -> (taskPrior.getStartTime().isEqual(start) &&
                            taskPrior.getEndTime().isEqual(end)) ||
                            taskPrior.getStartTime().isAfter(end) ||
                            taskPrior.getStartTime().isEqual(end) ||
                            taskPrior.getEndTime().isBefore(start) ||
                            taskPrior.getEndTime().isEqual(start));
        } else {
            return true;
        }
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
                prioritizedTasks.remove(subtask);
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
            if (task instanceof Subtask subtask) {
                historyManager.remove(subtask.getIdTask());
                prioritizedTasks.remove(subtask);
            }
        }
        subtaskMap.clear();
    }

    @Override
    public void removeAllTasks() {
        for (Task task : historyManager.getHistory()) {
            if (!(task instanceof Epic) && !(task instanceof Subtask)) {
                historyManager.remove(task.getIdTask());
                prioritizedTasks.remove(task);
            }
        }
        taskMap.clear();

    }

    @Override
    public void removeAllTypesOfTasks() {
        removeAllTasks();
        removeAllSubtasks();
        removeAllEpics();
        prioritizedTasks.clear();
    }

    @Override
    public void removeEpicById(int id) {
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
        }
        prioritizedTasks.remove(taskToRemove);
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
        if(!isNoOverLap(newSubtask)) {
            throw new TaskTimeOverLapException(timeOverLapError);
        }
        subtaskMap.replace(oldId, newSubtask);
        prioritizedTasks.remove(subtaskMap.get(oldId));
        addTaskByPriority(newSubtask);
        Epic epic = epicMap.get(newSubtask.getEpicIdTask());
        if (epic != null) {
            recalcEpicStatus(epic);
            epic.calculateEndTime();
        }
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        if(!isNoOverLap(newTask)) {
            throw new TaskTimeOverLapException(timeOverLapError);
        }
        taskMap.replace(oldId, newTask);
        prioritizedTasks.remove(taskMap.get(oldId));
        addTaskByPriority(newTask);
    }
}
