import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

public class Epic extends Task {
    private final Set<Integer> subtaskSet = new TreeSet<>();
    private LocalDateTime endTime;


    public Epic(String task, String description) {
        super(task, description);
    }

    public Epic(int idTask, String title, Status status, String description) {
        super(idTask, title, description, status);
    }

    public void calculateEndTime(Set<Subtask> subtaskSet) {
        if (subtaskSet.isEmpty()) {
            this.endTime = null;
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

        Duration duration = earlierStartTime != null && latestEndTime != null
                ? Duration.between(earlierStartTime, latestEndTime)
                : Duration.ZERO;

    }

    public void addSubtask(int idSubtask) {
        subtaskSet.add(idSubtask);
    }

    public void deleteSubtaskById(int idSubtask) {
        subtaskSet.remove(idSubtask);
    }

    public void deleteAllSubtasks() {
        subtaskSet.clear();
    }

    public Set<Integer> getSubtaskSet() {
        return subtaskSet;
    }

    @Override
    public String toString() {
        return "Epic{" + "subtaskSet=" + subtaskSet + ", idTask=" + this.getIdTask() + ", title='" + this.getTitle() + '\'' + ", description='" + this.getDescription() + '\'' + ", status=" + this.getStatus() + '}';
    }

    @Override
    public TypesOfTask getType() {
        return TypesOfTask.EPIC;
    }
}
