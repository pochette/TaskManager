public class Task {
    private static int id = 0;
    protected final int idTask;
    protected String title;
    protected String description;
    protected Status status;

    public Task(String title, String description, Status status) {
        idTask = incrementId();
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        idTask = incrementId();
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int idTask, String title, Status status, String description) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
    }


    private static int incrementId() {
        return id++;
    }

    public TypesOfTask getType() {
        return TypesOfTask.TASK;
    }


    @Override
    public String toString() {
        return "Task{" + "idTask=" + idTask + ", title='" + title + '\'' + ", description='" + description + '\'' + ", status=" + status + '}';
    }

    public String toString(Task task) {
        return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }

    public String getDescription() {
        return this.description;
    }

    public int getIdTask() {
        return idTask;
    }

    public void replaceTask(Task task) {
        this.status = task.getStatus();
        this.description = task.description;
        this.title = task.title;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return this.status;
    }

    public enum Status {
        NEW, DONE, IN_PROGRESS
    }
}
