import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private static int id = 0;
    private final int idTask;
    private final String title;
    private final String description;
    private Status status;
    private Duration duration;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(int idTask, String title, String description, Status status, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

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
    public Task(int idTask, String title, String description, Status status) {
        this.idTask = idTask;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    private static int incrementId() {
        return id++;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime calculateEndTime(Duration duration) {
        if (duration != null && this.getStartTime()!= null) {
            return this.getStartTime().plus(duration);
        }
        return null;
    }

    public LocalDateTime getEndTime() {
        return endTime;
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

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public enum Status {
        NEW, DONE, IN_PROGRESS
    }
}
