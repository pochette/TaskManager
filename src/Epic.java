import java.util.*;

public class Epic extends Task{
    protected Set<Integer> subtaskSet = new HashSet<>();

    public Epic(int id, String task, String description) {
        super(id, task, description);
        this.status = Status.NEW;
    }

    public void addSubtask (int id) {
        subtaskSet.add(id);
    }

    public Set<Integer> getSubtaskSet() {
        return subtaskSet;
    }


}
