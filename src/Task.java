public class Task {
    protected int id;
    protected String title;
    protected String description;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    protected Status status;
    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
        };

    public Task (String task, String description, Status status) {
        this.title = task;
        this.description = description;
        this.status = status;
        this.id = InMemoryTaskManager.incrementId();
    }
    public Task (String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.id = InMemoryTaskManager.incrementId();
    }

    public int getId () {
        return this.id;
    }
    public void replaceTask (Task task) {
        this.status = task.getStatus();
        this.description = task.description;
        this.title = task.title;

    }

    public Status getStatus () {
        return this.status;
    }
}
