import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static final Map<Integer, Task> taskMap = new HashMap<>();
    private static int id = 1; // генератор ID
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    static public int incrementId() {
        return id++;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    // -------------------- Создание --------------------
    @Override
    public void createTask(Task task) {
        task.setId(incrementId());
        taskMap.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(incrementId());
        taskMap.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(incrementId());
        Epic epic = (Epic) taskMap.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Эпик этого сабтаска не найден.");
            return;
        }
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        recalcEpicStatus(epic);
    }

    // -------------------- Получение --------------------
    @Override
    public Task getTask(int id) {
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
        if (!taskMap.containsKey(epic.getId())) {
            System.out.println("Эпик с ID " + epic.getId() + " не найден. Подзадачи не найдены.");
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
//        if (!taskMap.containsKey(newTask.getId())) {
//            System.out.println("Задача с ID " + newTask.getId() + " не найдена. Обновление не выполнено.");
//            return;
//        }
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
        if (!taskMap.containsKey(newSubtask.getId())) {
            System.out.println("Подзадача с ID " + newSubtask.getId() + " не найдена. Обновление не выполнено.");
            return;
        }

        taskMap.replace(oldId, newSubtask);

        Epic epic = (Epic) taskMap.get(newSubtask.getEpicId());
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
        if (!taskMap.containsKey(id)) {
            System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
            return;
        }
        taskMap.remove(id);
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

