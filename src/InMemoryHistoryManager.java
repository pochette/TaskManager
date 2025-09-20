import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> historyList = new LinkedList<>();

    //---------------------History-------------------------

    @Override
    public void addHistory(Task task) {
        if (historyList.size() > 10) {
            historyList.removeFirst();
            historyList.addLast(task);
            return;
        } else {
            historyList.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
