import java.util.HashMap;
import java.util.Map;

public class Epic extends Task{

    public Epic(int id, String task, String description) {
        super(id, task, description);
        status status = Epic.status.NEW;



    }



    Map<Integer, Epic> epicMap = new HashMap<>();

    public int getIdEpic () {
        return id;
    }

    public void updateEpic (Epic epic) {
        epicMap.put(epic.getIdEpic(), epic);
    }
}
