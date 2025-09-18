import java.util.*;

// TODO // --- updateTask ---
//public void updateTask(Task newTask) {
//    if (!taskMap.containsKey(newTask.getId())) {
//        System.out.println("Задача с ID " + newTask.getId() + " не найдена. Обновление не выполнено.");
//        return;
//    }
//    if (newTask instanceof Epic || newTask instanceof Subtask) {
//        System.out.println("Нельзя обновлять эпик/подзадачу этим методом.");
//        return;
//    }
//    taskMap.put(newTask.getId(), newTask);
//}
//
/// / --- updateEpic ---
//public void updateEpic(Epic newEpic) {
//    if (!taskMap.containsKey(newEpic.getId())) {
//        System.out.println("Эпик с ID " + newEpic.getId() + " не найден. Обновление не выполнено.");
//        return;
//    }
//    Epic oldEpic = (Epic) taskMap.get(newEpic.getId());
//    // переносим связь с подзадачами
//    newEpic.subtaskSet = oldEpic.getSubtaskSet();
//    recalcEpicStatus(newEpic);
//    taskMap.put(newEpic.getId(), newEpic);
//}
//

//// --- updateSubtask ---
//public void updateSubtask(Subtask newSubtask) {
//    if (!taskMap.containsKey(newSubtask.getId())) {
//        System.out.println("Подзадача с ID " + newSubtask.getId() + " не найдена. Обновление не выполнено.");
//        return;
//    }
//    taskMap.put(newSubtask.getId(), newSubtask);
//    Epic epic = (Epic) taskMap.get(newSubtask.getEpicId());
//    if (epic != null) {
//        recalcEpicStatus(epic);
//    }
//}

public class InMemoryTaskManager implements TaskManager {
    private static int id = 1; // генератор ID
    private Map<Integer, Task> taskMap = new HashMap<>();

    static public int incrementId() {
        return id++;
    }

    // -------------------- Создание --------------------

    public void createTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        taskMap.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        taskMap.put(subtask.getId(), subtask);
    }


//    public Task createTask(String title, String description) {
//        Task task = new Task(title, description, Task.Status.NEW);
//        taskMap.put(task.getId(), task);
//        return task;
//    }
//
//    public Task createTask(String title, String description, Task.Status status) {
//        Task task = new Task(title, description, status);
//        taskMap.put(task.getId(), task);
//        return task;
//    }
//
//    public Epic createEpic(String title, String description) {
//        Epic epic = new Epic(title, description);
//        taskMap.put(epic.getId(), epic);
//        return epic;
//    }
//
//    public Subtask createSubtask(String title, String description, Epic epic) {
//        if (!taskMap.containsKey(epic.getId())) {
//            System.out.println("Эпик с ID " + epic.getId() + " не найден. Подзадача не создана.");
//            return null;
//        }
//        Subtask subtask = new Subtask(title, description, epic.getId());
//        taskMap.put(subtask.getId(), subtask);
//        epic.addSubtask(subtask.getId());
//        recalcEpicStatus(epic);
//        return subtask;
//    }

//    public Subtask createSubtask(String title, String description, Epic epic, Task.Status status) {
//        if (!taskMap.containsKey(epic.getId())) {
//            System.out.println("Эпик с ID " + epic.getId() + " не найден. Подзадача не создана.");
//            return null;
//        }
//        Subtask subtask = new Subtask(title, description, epic.getId(), status);
//        taskMap.put(subtask.getId(), subtask);
//        epic.addSubtask(subtask.getId());
//        recalcEpicStatus(epic);
//        return subtask;
//    }

    // -------------------- Получение --------------------

    public Task getTask(int id) {
        return taskMap.get(id);
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Epic> getEpicList() {
        List<Epic> epics = new ArrayList<>();
        for (Task t : taskMap.values()) {
            if (t instanceof Epic) {
                epics.add((Epic) t);
            }
        }
        return epics;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtasks = new ArrayList<>();
        for (Task t : taskMap.values()) {
            if (t instanceof Subtask) {
                subtasks.add((Subtask) t);
            }
        }
        return subtasks;
    }

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
            }
        }
        return subtasks;
    }

    // -------------------- Обновление --------------------

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

    public void updateEpic(Epic newEpic, int oldId) {
//        if (!taskMap.containsKey(newEpic.getId())) {
//            System.out.println("Эпик с ID " + newEpic.getId() + " не найден. Обновление не выполнено.");
//            return;
//        }

        Epic oldEpic = (Epic) taskMap.get(oldId);

        // сохраняем связь с подзадачами
        newEpic.subtaskSet = oldEpic.getSubtaskSet();

        // статус пересчитывается только по сабтаскам
        recalcEpicStatus(newEpic);

        taskMap.replace(oldId, newEpic);
    }

    public void updateSubtask(Subtask newSubtask, int oldId) {
//        if (!taskMap.containsKey(newSubtask.getId())) {
//            System.out.println("Подзадача с ID " + newSubtask.getId() + " не найдена. Обновление не выполнено.");
//            return;
//        }

        taskMap.replace(oldId, newSubtask);

        Epic epic = (Epic) taskMap.get(newSubtask.getEpicId());
        if (epic != null) {
            recalcEpicStatus(epic);
        }
    }

    // -------------------- Удаление --------------------

    public void deleteAllTasks() {
        taskMap.clear();
    }

    public void deleteTaskId(int id) {
        if (!taskMap.containsKey(id)) {
            System.out.println("Задача с ID " + id + " не найдена. Удаление не выполнено.");
            return;
        }
        taskMap.remove(id);
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

