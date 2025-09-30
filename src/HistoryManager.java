import java.util.List;

interface HistoryManager {
   void add(Task task);
   void remove(int id);
   List<Task> getHistory();

}
