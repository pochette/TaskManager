import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public final class Managers {
  private static final HistoryManager historyManager = new InMemoryHistoryManager();
  private static final Path tasksStorageFile = Paths.get("src/Backend/Backend.csv");
  private static final CSV_TRANSFORMER CSV_TRANSFORMER = new CSV_TRANSFORMER();
  private static final TaskStorage defaultTaskStorageManager = createTaskStorage();
  private static final TaskManager taskManager = loadTaskManagerFromFile();

  private Managers() {}

  private static void addTasksToHistory(Map<Integer, ? extends Task> taskMap) {
    HistoryManager historyManager = getDefaultHistory();
    for (Task task : taskMap.values()) {
      historyManager.add(task);
    }
  }

  public static HistoryManager getDefaultHistory() {
    return historyManager;
  }

  public static TaskStorage createTaskStorage() {
    Function<Task, String> csvSerializer = CSV_TRANSFORMER::serializeTask;
    Function<String, Task> csvDeserializer = CSV_TRANSFORMER::getTaskFromLoad;
    return new FileTaskStorage(tasksStorageFile, csvSerializer, csvDeserializer);
  }

  public static TaskManager getDefault() {
    return taskManager;
  }

  public static TaskManager loadTaskManagerFromFile() {
    final List<Task> taskList = defaultTaskStorageManager.loadTasksFromFile();
    final HistoryManager historyManager = getDefaultHistory();
    final TaskManager loadedTaskManager =
        new FileBackedTaskManager(historyManager, defaultTaskStorageManager);

    for (Task task : taskList) {
      loadedTaskManager.createTask(task);
    }

    List<Integer> historyIds = loadHistoryIdsFromFile();
    for (Integer id : historyIds) {
      Task task = loadedTaskManager.getTaskById(id);
      if (task != null) {
        historyManager.add(task);
      }
    }

    return loadedTaskManager;
  }

  private static List<Integer> loadHistoryIdsFromFile() {
    String historyLine = "";
    try (Scanner scanner = new Scanner(tasksStorageFile)) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (!line.isBlank()) {
          historyLine = line;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    List<Integer> historyIds = new ArrayList<>();
    if (!historyLine.isBlank() && !historyLine.contains("type")) {
      String[] parts = historyLine.split(",");
      for (String id : parts) {
        if (!id.isBlank()) {
          historyIds.add(Integer.parseInt(id));
        }
      }
    }
    return historyIds;
  }

  private static Map<Integer, ? extends Task> splitTaskByType(List<Task> taskList) {
    final Map<Integer, Task> allTaskMap = new HashMap<>();
    for (Task task : taskList) {
      allTaskMap.put(task.getIdTask(), task);
    }
    return allTaskMap;
  }

  public static class CSV_TRANSFORMER {
    public Task getTaskFromLoad(String csvLine) {
      String[] fields = csvLine.split(",");
      int id = Integer.parseInt(fields[0]);
      TypesOfTask type = TypesOfTask.valueOf(fields[1]);
      String title = fields[2];
      Task.Status status = Task.Status.valueOf(fields[3]);
      String description = fields[4];

      Duration duration = null;
      if (fields.length > 6 && !fields[5].equals("null")) {
        duration = Duration.parse(fields[5]);
      }

      LocalDateTime startTime = null;
      if (fields.length > 6 && !fields[6].equals("null")) {
        startTime = LocalDateTime.parse(fields[6]);
      }

      return switch (type) {
        case TASK -> new Task(id, title, description, status, duration, startTime);
        case EPIC -> new Epic(id, title, description, status, duration, startTime);
        case SUBTASK -> {
          int epicId = Integer.parseInt(fields[7]);
          yield new Subtask(id, title, description, status, duration, startTime, epicId);
        }
      };
    }

    public String serializeTask(Task task) {
      if (!(task instanceof Subtask)) {
        return task.getIdTask()
            + ","
            + task.getType()
            + ","
            + task.getTitle()
            + ","
            + task.getStatus()
            + ","
            + task.getDescription()
            + ","
            + task.getDuration()
            + ","
            + task.getStartTime();
      } else {
        return task.getIdTask()
            + ","
            + task.getType()
            + ","
            + task.getTitle()
            + ","
            + task.getStatus()
            + ","
            + task.getDescription()
            + ","
            + task.getDuration()
            + ","
            + task.getStartTime()
            + ","
            + ((Subtask) task).getEpicIdTask();
      }
    }
  }
}
