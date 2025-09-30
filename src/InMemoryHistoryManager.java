import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private void removeNode(Node node) {
        if (node == null)
            return;
        if (node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;

        if (node.next != null)
            node.next.prev = node.prev;
        else
            tail = node.prev;
    }

    private void linkLast(Node node) {
        if (tail == null) {
            tail = head = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }

    }

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node current = head;
        while (current != null) {
            taskList.add(current.task);
            current = current.next;
        }
        return taskList;
    }

    @Override
    public void add(Task task) {
        if (task == null)
            return;
        Node node = new Node(task);
        remove(task.getId());
        linkLast(node);
        historyMap.put(task.getId(), node);
    }

    //---------------------History-------------------------

    @Override
    public void remove(int id) {
        Node node = historyMap.get(id);
        if (node == null) return;
        removeNode(node);
        historyMap.remove(id);

    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }
}
