import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> taskMap = new HashMap<>();// хранение всех задач
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    // -------------------- Создание --------------------
    @Override
    public void createTask(Task task) {
        taskMap.put(task.getIdTask(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        taskMap.put(epic.getIdTask(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        taskMap.put(subtask.getIdTask(), subtask);
        Epic epic = (Epic) taskMap.get(subtask.getEpicIdTask());
        if (epic == null) {
            System.out.println("Эпик этого сабтаска не найден.");
            return;
        }
        epic.addSubtask(subtask.getIdTask());
        recalcEpicStatus(epic);
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                "historyManager=" + historyManager +
                '}';
    }

    // -------------------- Получение --------------------
    @Override
    public Task getTaskById(int id) {
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public List<Task> getListOfAllTasks() {
        for (Task task : taskMap.values()) {
            historyManager.add(task);
        }
        return new ArrayList<>(taskMap.values());

    }

    @Override
    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Task t : taskMap.values()) {
            if (t instanceof Epic) {
                epicList.add((Epic) t);
                historyManager.add(t);
            }
        }
        return epicList;
    }

    @Override
    public List<Subtask> getSubtaskList() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Task t : taskMap.values()) {
            if (t instanceof Subtask) {
                subtasks.add((Subtask) t);
                historyManager.add(t);
            }
        }
        return subtasks;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        if (!taskMap.containsKey(epic.getIdTask())) {
            System.out.println("Эпик с ID " + epic.getIdTask() + " не найден. Подзадачи не найдены.");
            return Collections.emptyList();
        }
        List<Subtask> subtasks = new ArrayList<>();
        for (Integer subId : epic.getSubtaskSet()) {
            Task task = taskMap.get(subId);
            if (task instanceof Subtask) {
                subtasks.add((Subtask) task);
                historyManager.add(task);
            }
        }
        return subtasks;
    }

    // -------------------- Обновление --------------------

    @Override
    public void updateTask(Task newTask, int oldId) {
        if (newTask instanceof Epic) {
            System.out.println("Нельзя обновлять эпик этим методом.");
            return;
        }
        taskMap.replace(oldId, newTask);
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {

        Epic oldEpic = (Epic) taskMap.get(oldId);

        // сохраняем связь с подзадачами
        newEpic.subtaskSet = oldEpic.getSubtaskSet();

        // статус пересчитывается только по сабтаскам
        recalcEpicStatus(newEpic);

        taskMap.replace(oldId, newEpic);
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int oldId) {
        if (!taskMap.containsKey(newSubtask.getIdTask())) {
            System.out.println("Подзадача с ID " + newSubtask.getIdTask() + " не найдена. Обновление не выполнено.");
            return;
        }

        taskMap.replace(oldId, newSubtask);

        Epic epic = (Epic) taskMap.get(newSubtask.getEpicIdTask());
        if (epic != null) {
            recalcEpicStatus(epic);
        }
    }

    // -------------------- Удаление --------------------
    @Override
    public void deleteAllTasks() {
        taskMap.clear();
        historyManager.getHistory().clear();
    }

    @Override
    public void deleteTaskById(int id) {
        Task taskToRemove = taskMap.get(id);
        if (taskToRemove == null) {
            System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
        }
        if (taskToRemove instanceof Epic epic) {
            // Сначала удаляем все подзадачи этого эпика
            for (Integer subtaskId : epic.getSubtaskSet()) {
                taskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        } else if (taskToRemove instanceof Subtask subtask) {
            // Обновляем родительский эпик
            Epic epic = (Epic) taskMap.get(subtask.getEpicIdTask());
            epic.getSubtaskSet().remove(subtask.getIdTask());
            recalcEpicStatus(epic);
        }

        taskMap.remove(id); // Удаляем саму задачу/эпик
        historyManager.remove(id);
    }

    // -------------------- Вспомогательное --------------------

    private void recalcEpicStatus(Epic epic) {
        if (epic.getSubtaskSet().isEmpty()) {
            epic.status = Task.Status.NEW;
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : epic.getSubtaskSet()) {
            Subtask subtask = (Subtask) taskMap.get(subId);
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
            epic.status = Task.Status.NEW;
        } else if (allDone) {
            epic.status = Task.Status.DONE;
        } else {
            epic.status = Task.Status.IN_PROGRESS;
        }
    }
}
