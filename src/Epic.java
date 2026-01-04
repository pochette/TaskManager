import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Subtask> subtaskSet = new HashSet<>();
//    private LocalDateTime endTime;

    public Epic(String task, String description) {
        super(task, description, Status.NEW, TypesOfTask.EPIC);
        this.setEndTime(null);
        this.setStartTime(null);
        this.setDuration(Duration.ZERO);
    }

    public Epic(int idTask, String title, Status status, String description) {
        super(idTask, title, description, status, TypesOfTask.EPIC);
        this.setEndTime(null);
        this.setStartTime(null);
        this.setDuration(Duration.ZERO);
    }

    //конструктор для восстановления из файла
    public Epic(int idTask, String title, String description, Status status, Duration duration, LocalDateTime startTime,
                TypesOfTask typesOfTask) {
        super(idTask, title, description, status, duration, startTime, typesOfTask);
        setEndTime(calculateEndTime(duration));
    }

    public void addSubtask(Subtask subtask) {
        subtaskSet.add(subtask);
        calculateEndTime();
    }

    public void calculateEndTime() {
        if (this.getSubtaskSet().isEmpty()) {
            this.setEndTime(null);
            this.setStartTime(null);
            this.setDuration(Duration.ZERO);
            return;
        }

        LocalDateTime earlierStartTime = null;
        LocalDateTime latestEndTime = null;

        for (Subtask subtask : subtaskSet) {
            LocalDateTime subtaskStart = subtask.getStartTime();
            Duration subtaskDuration = subtask.getDuration();

            if (subtaskStart != null) {
                if (earlierStartTime == null || subtaskStart.isBefore(earlierStartTime)) {
                    earlierStartTime = subtaskStart;
                }
            }
            if (subtaskStart != null && subtaskDuration != null) {
                LocalDateTime endSubtask = subtaskStart.plus(subtaskDuration);
                if (latestEndTime == null || latestEndTime.isBefore(endSubtask)) {
                    latestEndTime = endSubtask;
                }
            }
        }
        this.setEndTime(latestEndTime);
        this.setStartTime(earlierStartTime);

        Duration duration = earlierStartTime != null && latestEndTime != null
                ? Duration.between(earlierStartTime, latestEndTime)
                : Duration.ZERO;
        setDuration(duration);
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
        if(subtaskSet.isEmpty()) {
            return Duration.ZERO;
        }
        return super.getDuration();
    }
    @Override
    public LocalDateTime getEndTime() {
        if (subtaskSet.isEmpty()) {
            return null;
        }
        return super.getEndTime();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskSet.isEmpty()) {
            return null;
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
