import java.util.HashMap;
import java.util.Map;

public class Epic extends Task{
    protected enum status {
        NEW,
        DONE,
        IN_PROGRESS
    };

    Map<Integer, Epic> epicMap = new HashMap<>();

    public int getIdEpic () {
        return id;
    }

    public void updateEpic (Epic epic) {
        epicMap.put(epic.getIdEpic(), epic);
    }
}
