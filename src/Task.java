public class Task {
    protected Integer id;
    protected String title;
    protected String description;


    public TypesOfTask getType() {
        return TypesOfTask.TASK;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toString(Task task) {
        return task.getId() + "," +
                task.getType() + "," +
                task.getTitle() + "," +
                task.getStatus() + "," +
                task.getDescription();
    }

    public String getDescription() {
        return this.description;
    }

    protected Status status;
    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
        };

    public Task (String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;

    }
    public Task (String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(Integer id, String title, Status status, String description)  {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }


    public int getId () {
        return this.id;
    }
    public void replaceTask (Task task) {
        this.status = task.getStatus();
        this.description = task.description;
        this.title = task.title;
    }

    public String getTitle () {
        return title;
    }
    public Status getStatus () {
        return this.status;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
