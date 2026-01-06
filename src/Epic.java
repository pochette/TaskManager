import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private static final LocalDateTime NULL_TIME = LocalDateTime.of(1, 1, 1, 1, 1);
    private final Set<Subtask> subtaskSet = new HashSet<>();
    private LocalDateTime endTime;

    public Epic(String title, Status status, String description) {
        super(title, description, status, Duration.ZERO, NULL_TIME);
        this.endTime = NULL_TIME;
    }

    //конструктор для восстановления из файла
    public Epic(int idTask, String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(idTask, title, description, status, duration, startTime);
        setTimeFields(startTime, NULL_TIME, duration);
        calculateEndTime();
    }

    public void addSubtask(Subtask subtask) {
        subtaskSet.add(subtask);
        calculateEndTime();
    }

    public void calculateEndTime() {
        if (this.getSubtaskSet().isEmpty()) {
            setTimeFields(NULL_TIME,NULL_TIME,Duration.ZERO);
            return;
        }

        LocalDateTime earlierStartTime = NULL_TIME;
        LocalDateTime latestEndTime = NULL_TIME;

        for (Subtask subtask : subtaskSet) {
            LocalDateTime subtaskStart = subtask.getStartTime();
            Duration subtaskDuration = subtask.getDuration();

            if (subtaskStart != NULL_TIME) {
                if (earlierStartTime == NULL_TIME || subtaskStart.isBefore(earlierStartTime)) {
                    earlierStartTime = subtaskStart;
                }
            }
            if (subtaskStart != NULL_TIME && subtaskDuration != Duration.ZERO) {
                LocalDateTime endSubtask = subtaskStart.plus(subtaskDuration);
                if (latestEndTime == NULL_TIME || latestEndTime.isBefore(endSubtask)) {
                    latestEndTime = endSubtask;
                }
            }
        }

        Duration duration = earlierStartTime != NULL_TIME && latestEndTime != NULL_TIME
                ? Duration.between(earlierStartTime, latestEndTime)
                : Duration.ZERO;
        setTimeFields(earlierStartTime, latestEndTime,duration);
    }

    public void deleteAllSubtasks() {
        subtaskSet.clear();
        calculateEndTime();
    }

    public void deleteSubtaskFromSubtasksSet(Subtask subtask) {
        subtaskSet.remove(subtask);
        calculateEndTime();
    }

    @Override
    public Duration getDuration() {
        if (subtaskSet.isEmpty()) {
            return Duration.ZERO;
        }
        return super.getDuration();
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtaskSet.isEmpty()) {
            return NULL_TIME;
        }
        return super.getEndTime();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskSet.isEmpty()) {
            return NULL_TIME;
        }
        return super.getStartTime();
    }

    public Set<Subtask> getSubtaskSet() {
        return subtaskSet;
    }

    @Override
    public TypesOfTask getType() {
        return TypesOfTask.EPIC;
    }

    private void setTimeFields(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.duration = duration;

    }
    @Override
    public String toString(Task task) {
        return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }

//    @Override
//    public String toString() {
//        return String.format("%d,%s,%s,%s,%s",
//                this.getIdTask(),
//                this.getType(),
//                this.getTitle(),
//                this.getStatus(),
//                this.getDescription());
//    }

//    @Override
//    public String toString() {
//        return "Epic{" + "subtaskSet=" + subtaskSet + ", idTask=" + this.getIdTask() + ", title='" + this.getTitle() + '\'' + ", description='" + this.getDescription() + '\'' + ", status=" + this.getStatus() + '}';
//    }
}
