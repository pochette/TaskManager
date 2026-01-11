import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Manager {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager(historyManager);
    private static final Path tasksStorageFile = Paths.get("src/Backend/Backend.csv");
    private static final TaskCSVTransformer taskCSVTransformer = new TaskCSVTransformer();
    private static final TaskStorageManager defaultTaskStorageManager = defaultTaskStorageManager();

    private Manager() {
    }

    public static TaskStorageManager defaultTaskStorageManager() {
        Function<Task, String> csvSerializer = taskCSVTransformer::taskToCSV;
        Function<String, Task> csvDeserializer = taskCSVTransformer::taskFromLoad;

        return new FileBasedTaskStorageManager(tasksStorageFile, csvSerializer, csvDeserializer);
    }

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }

    private static HistoryManager getHistoryManager(List<Task> taskList) {
        final HistoryManager historyManager = new InMemoryHistoryManager();
        splitTaskByType(taskList);
        return historyManager;
    }

    public static TaskManager loadTaskManagerFromFile() {
        final List<Task> taskList = defaultTaskStorageManager().loadFromFile();
        splitTaskByType(taskList);
        final HistoryManager historyManager = getHistoryManager(taskList);
        return new FileBackedTaskManager(historyManager, tasksStorageFile);
    }

    private static Map<Integer, ? extends Task> splitTaskByType(List<Task> taskList) {
        final Map<Integer, Task> taskMap = new HashMap<>();
        final Map<Integer, Subtask> subtaskMap = new HashMap<>();
        final Map<Integer, Epic> epicMap = new HashMap<>();


        taskList.stream()
                .filter(task -> String.valueOf(task.getIdTask()).startsWith("2"))
                .map(Task::getTitle)
                .sorted(Comparator.naturalOrder())
                .toList();


        for (Task task : taskList) {
            switch (task.getType()) {
                case TASK -> {
                    historyManager.add(task);
                    taskMap.put(task.getIdTask(), task);
                }
                case EPIC -> {
                    historyManager.add(task);
                    epicMap.put(task.getIdTask(), (Epic) task);
                }
                case SUBTASK -> {
                    historyManager.add(task);
                    subtaskMap.put(task.getIdTask(), (Subtask) task);
                }
                default -> throw new IllegalArgumentException();
            }
        }
        Map<Integer, Task> allTasksMap = new HashMap<>();
        allTasksMap.putAll(taskMap);
        allTasksMap.putAll(epicMap);
        allTasksMap.putAll(subtaskMap);

        return allTasksMap;
    }

    public static String taskToCSV(Task task) {
        if (!(task instanceof Subtask)) {
            return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                    "," + task.getDuration() + "," + task.getStartTime();
        } else {
            return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                    "," + task.getDuration() + "," + task.getStartTime() + "," + ((Subtask) task).getEpicIdTask();
        }
    }

    public static class TaskCSVTransformer {
        public String taskToCSV(Task task) {
            if (!(task instanceof Subtask)) {
                return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                        "," + task.getDuration() + "," + task.getStartTime();
            } else {
                return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                        "," + task.getDuration() + "," + task.getStartTime() + "," + ((Subtask) task).getEpicIdTask();
            }
        }
        public Task taskFromLoad(String value) {
            String[] valuesOfFields = value.split(",");
            int id = Integer.parseInt(valuesOfFields[0]);
            TypesOfTask typesOfTask = TypesOfTask.valueOf(valuesOfFields[1]);
            String title = valuesOfFields[2];
            Task.Status status = Task.Status.valueOf(valuesOfFields[3]);
            String description = valuesOfFields[4];

            Duration duration = null;
            if (valuesOfFields.length > 6 && !valuesOfFields[5].equals("null")) {
                duration = Duration.parse(valuesOfFields[5]);
            }

            LocalDateTime startTime = null;
            if (valuesOfFields.length > 6 && !valuesOfFields[6].equals("null")) {
                startTime = LocalDateTime.parse(valuesOfFields[6]);
            }

            return switch (typesOfTask) {
                case TASK -> new Task(id, title, description, status, duration, startTime);
                case EPIC -> new Epic(id, title,  description, status, duration, startTime);
                case SUBTASK -> {
                    int epicId = Integer.parseInt(valuesOfFields[7]);
                    yield new Subtask(id, title, description, status, duration, startTime,  epicId);
                }
            };
        }


    }


}
