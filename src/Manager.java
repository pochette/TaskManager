import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final  class Manager {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final Path tasksStorageFile = Paths.get("src/Backend/Backend.csv");
    private static final TaskCSVTransformer taskCSVTransformer = new TaskCSVTransformer();
    private static final TaskManager taskManager = loadTaskManagerFromFile();
    private static final TaskStorage defaultTaskStorageManager = defaultTaskStorageManager();

    private Manager() {
    }

    // defaultTaskStorageManager() нужен для создания экземпляра TaskStorageManager, который отвечает за сохранение и загрузку данных о задачах в файл. Это позволяет сохранять состояние задач между запусками программы, обеспечивая непрерывность работы с задачами и их историей.
    public static TaskStorage defaultTaskStorageManager() {
        Function<Task, String> csvSerializer = taskCSVTransformer::taskToCSV;
        Function<String, Task> csvDeserializer = taskCSVTransformer::taskFromLoad;
        return new FileTaskStorage(tasksStorageFile, csvSerializer, csvDeserializer);
    }

    // getDefault() нужен для получения единственного экземпляра TaskManager, который используется в программе. Это позволяет обеспечить единый доступ к TaskManager и его данным, обеспечивая удобство и непрерывность работы с задачами между различными частями программы.
    public static TaskManager getDefault() {
        return taskManager;
    }


    // getHistoryManager() нужен для получения единственного экземпляра HistoryManager, который используется в программе. Это позволяет обеспечить единый доступ к HistoryManager и его данным, обеспечивая удобство и непрерывность работы с историей просмотров задач между различными частями программы.
    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }


    //loadTaskManagerFromFile() нужен для загрузки данных из файла при запуске программы, чтобы не потерять данные между запусками
    // Он читает данные из файла, преобразует их в объекты Task и добавляет их в TaskManager, а также в HistoryManager для сохранения истории просмотров
    // Это позволяет сохранять состояние задач и историю просмотров между запусками программы, обеспечивая непрерывность работы с задачами
    public static TaskManager loadTaskManagerFromFile() {
        final List<Task> taskList = defaultTaskStorageManager().loadFromFile();
        splitTaskByType(taskList);
        final HistoryManager historyManager = getHistoryManager(taskList);
        return new FileBackedTaskManager(historyManager, defaultTaskStorageManager);
    }

    //getHistoryManager() нужен для создания экземпляра HistoryManager и добавления в него задач из списка, который был загружен из файла. Это позволяет сохранить историю просмотров задач при загрузке данных из файла, обеспечивая непрерывность работы с задачами и их историей между запусками программы.
    // Он принимает список задач, загруженных из файла, и добавляет каждую задачу в HistoryManager, чтобы сохранить историю просмотров. Это позволяет пользователю видеть историю просмотров задач даже после перезапуска программы, обеспечивая удобство и непрерывность работы с задачами.
    private static HistoryManager getHistoryManager(List<Task> taskList) {
        final HistoryManager historyManager = new InMemoryHistoryManager();
        splitTaskByType(taskList);
        return historyManager;
    }

    // splitTaskByType() нужен для разделения задач на три категории: Task, Subtask и Epic, и сохранения их в отдельных коллекциях. Это позволяет эффективно управлять задачами в TaskManager, обеспечивая удобный доступ к каждой категории задач и их истории просмотров.
    // Он принимает список задач, загруженных из файла, и разделяет их на три категории: Task, Subtask и Epic, используя их типы. Каждая категория задач сохраняется в отдельной коллекции (Map), что позволяет эффективно управлять задачами в TaskManager. Кроме того, каждая задача добавляется в HistoryManager, чтобы сохранить историю просмотров. Это обеспечивает удобный доступ к каждой категории задач и их истории просмотров, улучшая функциональность TaskManager и обеспечивая непрерывность работы с задачами между запусками программы.

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

//    //taskToCSV()
//    public static String taskToCSV(Task task) {
//        if (!(task instanceof Subtask)) {
//            return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
//                    "," + task.getDuration() + "," + task.getStartTime();
//        } else {
//            return task.getIdTask() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription() +
//                    "," + task.getDuration() + "," + task.getStartTime() + "," + ((Subtask) task).getEpicIdTask();
//        }
//    }

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
                case EPIC -> new Epic(id, title, description, status, duration, startTime);
                case SUBTASK -> {
                    int epicId = Integer.parseInt(valuesOfFields[7]);
                    yield new Subtask(id, title, description, status, duration, startTime, epicId);
                }
            };
        }


    }


}
