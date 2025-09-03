import java.util.*;

public class Manager {
    private static int id = 0;//Для генерации идентификаторов можно использовать числовое поле класса менеджер, увеличиваия его на
    //нужно получить новое значение

    Map<Integer, Task> taskMap = new HashMap<>();

    public Task createTask (String title, String description, Task.Status status) {
        Task task = new Task(id++, title, description, status);
        taskMap.put(task.getId(), task);
        return task;
    }

    public Epic createEpic (String title, String description) {
        Epic epic = new Epic(id++, title, description);
        taskMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask (String title, String description, Epic epic, Task.Status status) {
        Subtask subtask = new Subtask(id++, title, description, epic.getId(), status);
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        return subtask;
    }

    public Subtask createSubtask (String title, String description, Epic epic) {
        Subtask subtask = new Subtask(id++, title, description, epic.getId());
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        return subtask;
    }

    public Task getTask (int id) {
        return taskMap.get(id);
    }

    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Task) {
                taskList.add(value);
            }
        }
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Epic) {
                epicList.add((Epic) value);
            }
        }
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Subtask) {
                subtaskList.add( (Subtask) value);
            }
        }
        return subtaskList;
    }

    public void deleteAllTasks () {
        taskMap.clear();
    }

    public void updateTask (int id, Task task) {
        taskMap.replace(id, task);
    }


//    Добавить потом в каждое обновление статусов сабтасков
    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskSet().isEmpty()) {
            epic.updateTasksStatus(Task.Status.NEW);
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

            if (allNew) {
                epic.updateTasksStatus(Task.Status.NEW);
            } if (allDone) {
                epic.updateTasksStatus(Task.Status.DONE);
            } else {
                epic.updateTasksStatus(Task.Status.IN_PROGRESS);
            }
        }
    }

    public void updateStatus (int id, Task task, Task.Status status) {
        Task tempTask = taskMap.get(id);
        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
        } else if (tempTask instanceof Subtask || tempTask instanceof Task) {
            tempTask.updateTasksStatus(status);
            taskMap.replace(id, tempTask);
        } else {

        }
    }

    public void deleteTaskId (int id) {
        taskMap.remove(id);
    }
    public Set<Integer> getSubtasksOfEpic (Epic epic) {
        Set<Integer> subtaskSet = new HashSet<>(epic.getSubtaskSet());
        return subtaskSet;
    }






}
