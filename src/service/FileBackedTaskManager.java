package service;

import model.Epic;
import model.Subtask;
import model.Task;

public class FileBackedTaskManager extends InMemoryTaskManager {

  private final TaskStorage taskStorageManager;

  public FileBackedTaskManager(HistoryManager historyManager, TaskStorage taskStorageManager) {
    super(historyManager);
    this.taskStorageManager = taskStorageManager;
  }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id); // Вызов родительского метода, который добавляет в историю
        taskStorageManager.save(this.getListOfAllTasks()); // Сохранение после изменения
        return task;
    }

    @Override
    public void removeAllOrdinaryTasks() {
        super.removeAllOrdinaryTasks();
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void removeAllTypesOfTasks() {
        super.removeAllTypesOfTasks();
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {
        super.updateEpic(newEpic, oldId);
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int oldId) {
        super.updateSubtask(newSubtask, oldId);
        taskStorageManager.save(this.getListOfAllTasks());
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        super.updateTask(newTask, oldId);
        taskStorageManager.save(this.getListOfAllTasks());
    }
}
