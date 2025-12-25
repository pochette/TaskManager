import java.util.*;

public class Epic extends Task {
    protected Set<Integer> subtaskSet = new HashSet<>();

    public Epic(String task, String description) {
        super(task, description);
    }

    public Epic(int idTask, String title, Status status, String description) {
        super(idTask, title, status, description);
    }

    public void addSubtask(int idSubtask) {
        subtaskSet.add(idSubtask);
    }

    public Set<Integer> getSubtaskSet() {
        return subtaskSet;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskSet=" + subtaskSet +
                ", idTask=" + this.getIdTask() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public TypesOfTask getType() {
        return TypesOfTask.EPIC;
    }
}
