package service;

import model.Task;

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

  public FileTaskStorage(
          Path path, Function<Task, String> serializer, Function<String, Task> deserializer) {
    validatePath(path);
    this.path = path;
    this.serializer = serializer;
    this.deserializer = deserializer;
  }

  private void validatePath(Path path) {
    if (path == null) {
      throw new TaskManagerRuntimeException("Путь к файлу не может быть null.");
    }
    if (!Files.exists(path)) {
      throw new TaskManagerRuntimeException("Файл не существует: " + path);
    }
    if (Files.isDirectory(path)) {
      throw new TaskManagerRuntimeException("Ожидался файл, но указан каталог: " + path);
    }
    Path parent = path.getParent();
    if (parent != null && !Files.exists(parent)) {
      throw new TaskManagerRuntimeException("Каталог для файла не существует: " + parent);
    }
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
      throw new ManagerReadException("Не удалось прочитать список задач из файла",  e.getCause());
    }
    return tasks;
  }

  @Override
  public void save(Collection<Task> tasks) throws ManagerSaveException {
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
      writer.write("id,type,name,status,description,duration,startTime,epic");
      writer.newLine();

      for (Task task : tasks) {
        writer.write(serializer.apply(task));
        writer.newLine();
      }
      writer.newLine();
      // история просмотров должна быть в виде:
      // 1,2,3,...
      String historyLine =
          Managers.getDefaultHistory().getHistory().stream()
              .map(Task::getIdTask)
              .map(String::valueOf)
              .collect(Collectors.joining(","));
      writer.write(historyLine);

      writer.flush();
    } catch (IOException e) {
      throw new ManagerSaveException("Ошибка при сохранении в файл: " + path.getFileName(), e);
    }
  }
}
