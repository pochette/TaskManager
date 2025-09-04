import java.util.*;

public class Epic extends Task{
    protected Set<Integer> subtaskSet = new HashSet<>();

    public Epic(String task, String description) {
        super(task, description);
        this.status = Status.NEW;
    }

    public void addSubtask (int id) {
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
}
