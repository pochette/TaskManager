public class Task {
    private int id = 0;
    protected String title;
    protected String description;
    protected Status status;
    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
        };

    public Task (int id, String task, String description, Status status) {
        this.title = task;
        this.description = description;
        this.status = status;
    }
    public Task (int id, String task, String description) {
        this.title = task;
        this.description = description;
        this.status = Status.NEW;
    }

    public int getId () {
        return this.id;
    }
    public void updateTasksStatus(Status status) {
        this.status = status;
    }

    public Status getStatus () {
        return this.status;
    }
}
