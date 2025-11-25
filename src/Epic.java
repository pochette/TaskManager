import java.util.*;

public class Epic extends Task {
    protected Set<Integer> subtaskSet = new HashSet<>();

    public Epic(String task, String description) {
        super(task, description);
    }

    public Epic(Integer id, String title, Status status, String description) {
        super(id, title, status, description);
    }

    public void addSubtask(int id) {
        subtaskSet.add(id);
    }

    public Set<Integer> getSubtaskSet() {
        return subtaskSet;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskSet=" + subtaskSet +
                ", id=" + id +
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
