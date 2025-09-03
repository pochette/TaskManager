import java.util.HashMap;
import java.util.Map;

public class Epic extends Task{

    public Epic(int id, String task, String description) {
        super(id, task, description);
        Status status = Epic.Status.NEW;
    }

}
