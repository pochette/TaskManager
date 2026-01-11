import java.io.IOException;

public class NewFileBackedTaskManager extends InMemoryTaskManager {
    private final TaskStorageManager taskStorageManager;

    public NewFileBackedTaskManager(HistoryManager historyManager, TaskStorageManager taskStorageManager) {
        super(historyManager);
        this.taskStorageManager = taskStorageManager;
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void recalcEpicStatus(Epic epic) {
        super.recalcEpicStatus(epic);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllTypesOfTasks() {
        super.removeAllTypesOfTasks();
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {
        super.updateEpic(newEpic, oldId);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int oldId) {
        super.updateSubtask(newSubtask, oldId);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        super.updateTask(newTask, oldId);
        try {
            taskStorageManager.save(this.getListOfAllTasks());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
