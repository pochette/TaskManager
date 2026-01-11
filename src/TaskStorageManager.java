import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface TaskStorageManager {
    void save(Collection<Task> tasks) throws IOException;
    List<Task> loadFromFile();
}
