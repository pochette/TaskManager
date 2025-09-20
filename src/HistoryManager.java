import java.util.List;

interface HistoryManager {
   void addHistory(Task task);
   List<Task> getHistory();

}
