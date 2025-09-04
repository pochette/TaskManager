import java.util.*;

/**
 * Manages tasks, epics, and subtasks.
 * This class is responsible for creating, retrieving, updating, and deleting tasks.
 */
public class Manager {
    /**
     * Counter for generating unique task IDs.
     */
    private static int id = 0;
    /**
     * Storage for all tasks, epics, and subtasks, mapped by their ID.
     */
    private final Map<Integer, Task> taskMap = new HashMap<>();

    /**
     * Creates a new Task.
     * @param title The title of the task.
     * @param description The description of the task.
     * @return The created Task object.
     */
    public Task createTask (String title, String description) {
        Task task = new Task(id++, title, description, Task.Status.NEW);
        taskMap.put(task.getId(), task);
        return task;
    }

    /**
     * Creates a new Epic.
     * @param title The title of the epic.
     * @param description The description of the epic.
     * @return The created Epic object.
     */
    public Epic createEpic (String title, String description) {
        Epic epic = new Epic(id++, title, description);
        taskMap.put(epic.getId(), epic);
        return epic;
    }

    /**
     * Creates a new Subtask and associates it with an Epic.
     * @param title The title of the subtask.
     * @param description The description of the subtask.
     * @param epic The parent Epic of this subtask.
     * @return The created Subtask object.
     */
    public Subtask createSubtask (String title, String description, Epic epic) {
        Subtask subtask = new Subtask(id++, title, description, epic.getId());
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        return subtask;
    }

    // Commented out method for creating a subtask with a specific status.
//    public Subtask createSubtask (String title, String description, Epic epic, Task.Status status) {
//        Subtask subtask = new Subtask(id++, title, description, epic.getId(), status);
//        taskMap.put(subtask.getId(), subtask);
//        epic.addSubtask(subtask.getId());
//        return subtask;
//    }

    /**
     * Retrieves a task (or Epic, or Subtask) by its ID.
     * @param id The ID of the task to retrieve.
     * @return The Task object if found, otherwise null.
     */
    public Task getTask (int id) {
        return taskMap.get(id);
    }

    /**
     * @return A list of all tasks (including Epics and Subtasks).
     */
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Task) {
                taskList.add(value);
            }
        }
        return taskList;
    }

    /**
     * @return A list containing only the Epics.
     */
    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Epic) {
                epicList.add((Epic) value);
            }
        }
        return epicList;
    }

    /**
     * @return A list containing only the Subtasks.
     */
    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Subtask) {
                subtaskList.add( (Subtask) value);
            }
        }
        return subtaskList;
    }

    /**
     * Deletes all tasks, epics, and subtasks from the manager.
     */
    public void deleteAllTasks () {
        taskMap.clear();
    }

    /**
     * Updates a task in the map with a new Task object.
     * @param id The ID of the task to update.
     * @param task The new Task object to replace the old one.
     */
    public void updateTask (int id, Task task) {
        taskMap.replace(id, task);
    }

    /**
     * Updates the status of an Epic based on the statuses of its Subtasks.
     * - If all subtasks are NEW, the epic is NEW.
     * - If all subtasks are DONE, the epic is DONE.
     * - Otherwise, the epic is IN_PROGRESS.
     * @param epic The Epic to update.
     */
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
        }
        if (allNew) {
            epic.updateTasksStatus(Task.Status.NEW);
        } else if (allDone) {
            epic.updateTasksStatus(Task.Status.DONE);
        } else {
            epic.updateTasksStatus(Task.Status.IN_PROGRESS);
        }
    }

    /**
     * Updates the status of a given task or subtask. If the ID belongs to an Epic, its status is recalculated.
     * @param id The ID of the task to update.
     * @param status The new status.
     */
    public void updateStatus (int id, Task.Status status) {
        Task tempTask = taskMap.get(id);
        if (tempTask == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
        } else if ( !(tempTask instanceof Epic)) {
            tempTask.updateTasksStatus(status);
            taskMap.replace(id, tempTask);
        } else {
            updateEpicStatus((Epic)tempTask);
        }
    }

    /**
     * Deletes a task by its ID.
     * @param id The ID of the task to delete.
     */
    public void deleteTaskId (int id) {
        taskMap.remove(id);
    }

    /**
     * Retrieves all Subtasks associated with a specific Epic.
     * @param epic The Epic whose subtasks are to be retrieved.
     * @return A list of Subtasks.
     */
    public List<Subtask> getSubtasksOfEpic (Epic epic) {
        Set<Integer> subtaskSet = new HashSet<>(epic.getSubtaskSet());
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subId : subtaskSet) {
            subtaskList.add((Subtask) taskMap.get(subId));
        }
        return subtaskList;
    }
}