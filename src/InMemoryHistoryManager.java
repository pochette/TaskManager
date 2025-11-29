import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    public static String toString(HistoryManager historyManager) {
        return historyManager.toString();
    }

    private void removeNode(Node node) {
        if (node == null) return;

        Node prev = node.prev;
        Node next = node.next;

        if (prev != null) prev.next = next;
        else head = next;

        if (next != null) next.prev = prev;
        else tail = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return Objects.equals(head, that.head) && Objects.equals(tail, that.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
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

    @Override
    public List<Task> getHistory() {
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
        if (task == null) return;
        remove(task.getId());
        Node node = new Node(task);
        linkLast(node);
        historyMap.put(task.getId(), node);
    }

    //---------------------History-------------------------

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" + "head=" + head + ", tail=" + tail + '}';
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
