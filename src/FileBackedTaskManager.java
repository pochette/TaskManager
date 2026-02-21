public class FileBackedTaskManager extends InMemoryTaskManager {

  private final TaskStorage taskStorageManager;

  public FileBackedTaskManager(HistoryManager historyManager, TaskStorage taskStorageManager) {
    super(historyManager);
    this.taskStorageManager = taskStorageManager;
  }

  @Override
  public void createEpic(Epic epic) {
    super.createEpic(epic);
    taskStorageManager.save(this.getListOfAllTasks());
  }

  @Override
  public void createSubtask(Subtask subtask) {
    super.createSubtask(subtask);
    taskStorageManager.save(this.getListOfAllTasks());
  }

  @Override
  public void createTask(Task task) {
    super.createTask(task);
    taskStorageManager.save(this.getListOfAllTasks());
  }

  @Override
  public void recalcEpicStatus(Epic epic) {
    super.recalcEpicStatus(epic);
    taskStorageManager.save(this.getListOfAllTasks());
  }

  @Override
  public void removeAllEpics() {
    super.removeAllEpics();
    taskStorageManager.save(this.getListOfAllTasks());
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
  public void removeEpicById(int id) {
    super.removeEpicById(id);
    taskStorageManager.save(this.getListOfAllTasks());
  }

  @Override
  public void removeSubtaskById(int id) {
    super.removeSubtaskById(id);
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
