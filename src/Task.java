import java.util.HashMap;
import java.util.Map;

public class Task {
    protected int id;
    protected String task;
    protected String description;
    protected Status status;
    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
        };


    Map<Integer, Task> taskMap = new HashMap<>();

    public Task (int id, String task, String description) {
        this.id = Manager.setId();
        this.task = task;
        this.description = description;;
        this.status = Status.NEW;
        taskMap.put(id, this);
    }

    public void setStatus () {
        Status aNew = Status.NEW;
    }
}
