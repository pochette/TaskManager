import java.util.Collection;
import java.util.List;

public interface TaskStorage {
    void save(Collection<Task> tasks);
    List<Task> loadFromFile();
}
