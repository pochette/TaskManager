import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int id = 0;
    private final int idTask;
    private final String title;
    private final String description;
    protected Duration duration;
    protected LocalDateTime startTime;
    private Status status;

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.idTask = incrementId();
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // конструктор для восстановления из файла
    public Task(int idTask, String title, String description, Status status, Duration duration,
                LocalDateTime startTime) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
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
        return idTask == task.idTask;
    }

    public String getDescription() {
        return this.description;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
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
        return TypesOfTask.TASK;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask);
    }

    @Override
    public String toString() {
        return getType() + "{" +
                "idTask=" + idTask +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", typesOfTask=" + getType() +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                '}';
    }

    public String toString(Task task) {
        return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                "," + task.getDuration() + "," + task.getStartTime();
    }

    public enum Status {
        NEW, DONE, IN_PROGRESS
    }

}
