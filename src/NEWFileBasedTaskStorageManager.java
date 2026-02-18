import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class NEWFileBasedTaskStorageManager implements TaskStorageManager {
    private final Path path;
    private final Function<Task, String> serializer;
    private final Function<String, Task> deserializer;

    public NEWFileBasedTaskStorageManager(Path path, Function<Task, String> serializer, Function<String, Task> deserializer) {
        this.path = path;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public List<Task> loadFromFile() {
        List<Task> tasks = new ArrayList<>();

        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNext()) {
                final Task task = deserializer.apply((scanner.nextLine()));
                tasks.add(task);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return tasks;
    }

    @Override
    public void save(Collection<Task> tasks) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8 )) {
            for (Task task : tasks) {
                writer.write(serializer.apply(task));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
