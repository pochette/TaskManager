import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> historyList = new LinkedList<>();

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;


    private void removeNode(Task task) {

    }

    private void linkLast () {

    }

    private List<Task> getTasks () {

    }


    //---------------------History-------------------------

    @Override
    public void add(Task task) {
        if (historyList.size() > 9) {
            historyList.removeFirst();
            historyList.addLast(task);
        } else if ()
        else {
            historyList.addLast(task);
        }
    }

    @Override
    public void remove(int id) {
        historyList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
