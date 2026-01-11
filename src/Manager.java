import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Manager {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager(historyManager);
    private static final Path tasksStorageFile = Paths.get("src/Backend/Backend.csv");
    static TaskCSVTransformer taskCSVTransformer = new TaskCSVTransformer();
    private static final TaskStorageManager defaultTaskStorageManager = defaultTaskStorageManager();

    private Manager() {
    }

    public static TaskStorageManager defaultTaskStorageManager() {
        Function<Task, String> csvSerializer = taskCSVTransformer::taskToCSV;

        return new FileBasedTaskStorageManager(tasksStorageFile, null, null);
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

    static class TaskCSVTransformer {
        public String taskToCSV(Task task) {
            if (!(task instanceof Subtask)) {
                return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                        "," + task.getDuration() + "," + task.getStartTime();
            } else {
                return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
                        "," + task.getDuration() + "," + task.getStartTime() + "," + ((Subtask) task).getEpicIdTask();
            }
        }
    }

}
