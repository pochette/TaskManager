import java.util.HashMap;
import java.util.Map;

public class Task {
    private int id;
    protected String title;
    protected String description;
    protected Status status;
    public enum Status {
        NEW,
        DONE,
        IN_PROGRESS
        };

    public Task (int id, String task, String description) {
        this.title = task;
        this.description = description;;
        this.status = Status.NEW;
    }

    public int getId () {
        return this.getId();
    }
}
