import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int id = 0;
    private final int idTask;
    private final String title;
    private final String description;
    private final TypesOfTask typesOfTask;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String title, String description, TypesOfTask typesOfTask, Status status, Duration duration, LocalDateTime startTime) {
        this.idTask = incrementId();
        this.title = title;
        this.description = description;
        this.typesOfTask = typesOfTask;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime(duration);
    }

    // конструктор для восстановления из файла
    public Task(int idTask, String title, String description, Status status, Duration duration,
                LocalDateTime startTime, TypesOfTask typesOfTask) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.typesOfTask = typesOfTask;
        this.endTime = calculateEndTime(duration);
    }

    public Task(String title, String description, Status status, TypesOfTask typesOfTask) {
        this.idTask = incrementId();
        this.title = title;
        this.description = description;
        this.status = status;
        this.typesOfTask = typesOfTask;
    }

    public Task(String title, String description) {
        this.idTask = incrementId();
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.typesOfTask = TypesOfTask.TASK;
    }

    public Task(int idTask, String title, String description, Status status, TypesOfTask typesOfTask) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
        this.typesOfTask = typesOfTask;
    }

    private static int incrementId() {
        return id++;
    }

    public LocalDateTime calculateEndTime(Duration duration) {
        if (duration != null && this.getStartTime() != null) {
            return this.getStartTime().plus(duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) && Objects.equals(description, task.description) && typesOfTask == task.typesOfTask && status == task.status && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime) && Objects.equals(endTime, task.endTime);
    }

    public String getDescription() {
        return this.description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getIdTask() {
        return idTask;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public TypesOfTask getType() {
        return typesOfTask;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, typesOfTask, status, duration, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Task{" + "idTask=" + idTask + ", title='" + title + '\'' + ", description='" + description + '\'' + ", status=" + status + '}';
    }

    public String toString(Task task) {
        return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }

    public enum Status {
        NEW, DONE, IN_PROGRESS
    }

}
