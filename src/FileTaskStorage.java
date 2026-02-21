import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileTaskStorage implements TaskStorage {
    private final Path path;
    private final Function<Task, String> serializer;
    private final Function<String, Task> deserializer;

    public FileTaskStorage(Path path, Function<Task, String> serializer, Function<String, Task> deserializer) {
        this.path = path;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public List<Task> loadTasksFromFile() {
        List<Task> tasks = new ArrayList<>();

        try (Scanner scanner = new Scanner(path)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Пропускаем заголовок
            }

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
    public void save(Collection<Task> tasks) throws ManagerSaveException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8 )) {
            writer.write("id,type,name,status,description,duration,startTime,epic");
            writer.newLine();

            for (Task task : tasks) {
                writer.write(serializer.apply(task));
                writer.newLine();
            }
            writer.newLine();
            //история просмотров должна быть в виде:
            // 1,2,3,...

            writer.write(Manager.getDefaultHistory().getHistory().stream()
                    .map(Task::getIdTask)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","))
            );

            writer.flush();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + path.getFileName(), e);
        }

    }

}
