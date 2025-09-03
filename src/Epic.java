import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Epic extends Task{
    protected List<Integer> subtaskList = new ArrayList<>();

    public Epic(int id, String task, String description) {
        super(id, task, description);
        Status status = Epic.Status.NEW;
    }

    public void addSubtask (int id) {
        subtaskList.add(id);
    }

    public List<Integer> getSubtaskList() {
        return subtaskList;
    }

}
