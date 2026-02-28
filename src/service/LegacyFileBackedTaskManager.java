//import java.io.IOException;
//import java.io.Writer;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.*;
//
//public class LegacyFileBackedTaskManager extends InMemoryTaskManager {
//    private final Path file;
//
//    public LegacyFileBackedTaskManager(HistoryManager historyManager, Path path) {
//        super(historyManager);
//        this.file = path;
//        createFileIfNotExist();
//        loadFromFile(path);
//    }
//
//    //id,type,name,status,description,duration,startTime,endTime,epic
//    public static String taskToString2(model.Task task) {
//
//        String string = String.format("%d,%s,%s,%s,%s,%s,%s",
//                task.getIdTask(),
//                task.getType(),
//                task.getTitle(),
//                task.getStatus(),
//                task.getDescription(),
//                task.getDuration() != null ? task.getDuration() : "null",
//                task.getStartTime() != null ? task.getStartTime() : "null");
//        if (task instanceof model.Subtask subtask) {
//            string = String.join(",", string, Integer.toString(subtask.getEpicIdTask()));
//        }
//        return string;
//    }
//
//    @Override
//    public void createEpic(model.Epic epic) {
//        super.createEpic(epic);
//        save();
//    }
//
//    private void createFileIfNotExist() throws ManagerSaveException {
//        try {
//            if (!Files.exists(file)) {
//                if (!Files.exists(file.getParent())) {
//                    Files.createDirectories(file.getParent());
//                }
//                Files.createFile(file);
//                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
//                    writer.write("id,type,name,status,description,duration,startTime,epic\n");
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось создать файл: " + file, e);
//        }
//    }
//
//    @Override
//    public void createSubtask(model.Subtask subtask) {
//        super.createSubtask(subtask);
//        save();
//    }
//
//    @Override
//    public void createTask(model.Task task) {
//        super.createTask(task);
//        save();
//
//    }
//
//    @Override
//    public model.Epic getEpicById(int id) {
//        model.Epic epic = super.getEpicById(id);
//        save();
//        return epic;
//    }
//
//    @Override
//    public List<model.Epic> getEpicList() {
//        List<model.Epic> epics = super.getEpicList();
//        save();
//        return epics;
//    }
//
//    @Override
//    public List<model.Task> getListOfAllTasks() {
//        List<model.Task> allTasks = super.getListOfAllTasks();
//        save();
//        return allTasks;
//    }
//
//    @Override
//    public model.Subtask getSubtaskById(int id) {
//        model.Subtask subtask = super.getSubtaskById(id);
//        save();
//        return subtask;
//    }
//
//    @Override
//    public List<model.Subtask> getSubtaskList() {
//        List<model.Subtask> subtasks = super.getSubtaskList();
//        save();
//        return subtasks;
//    }
//
//    @Override
//    public List<model.Subtask> getSubtasksOfEpic(int epicId) {
//        List<model.Subtask> subtasksOfEpic = super.getSubtasksOfEpic(epicId);
//        save();
//        return subtasksOfEpic;
//    }
//
//    @Override
//    public model.Task getTaskById(int id) {
//        model.Task task = super.getTaskById(id);
//        save();
//        return task;
//    }
//
//    @Override
//    public List<model.Task> getTaskList() {
//        List<model.Task> tasks = super.getTaskList();
//        save();
//        return tasks;
//    }
//
//    @Override
//    public Map<Integer, model.Task> getTaskMap() {
//        Map<Integer, model.Task> taskMap = super.getTaskMap();
//        save();
//        return taskMap;
//    }
//
//    public void loadFromFile(Path path) throws ManagerSaveException {
//        if (!Files.exists(path)) {
//            return;
//        }
//        try {
//            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
//            for (String line : lines.stream().skip(1).toList()) {
//                if (line.isBlank()) {
//                    break;
//                }
//                model.Task task = taskFromLoad(line);
//                switch (task.getType()) {
//                    case TASK:
//                        super.createTask(task);
//                        break;
//                    case EPIC:
//                        super.createEpic((model.Epic) task);
//                        break;
//                    case SUBTASK:
//                        super.createSubtask((model.Subtask) task);
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            throw new ManagerSaveException("Не удалось загрузить данные из файла", e);
//        }
//    }
//
//    @Override
//    public void removeAllEpics() {
//        super.removeAllEpics();
//        save();
//    }
//
//    @Override
//    public void removeAllOrdinaryTasks() {
//        super.removeAllOrdinaryTasks();
//        save();
//    }
//
////    private String taskToString(model.Task task) {
////        String epicIdField = "";
////        if (task instanceof model.Subtask) {
////            epicIdField = String.valueOf(((model.Subtask) task).getEpicIdTask());
////        }
////        return String.join(",",
////                String.valueOf(task.getIdTask()),
////                task.getType().toString(),
////                task.getTitle(),
////                task.getStatus().toString(),
////                task.getDescription(),
////                task.getDuration().toString(),
////                task.getStartTime().toString(),
//
//    @Override
//    public void removeAllSubtasks() {
//        super.removeAllSubtasks();
//        save();
//    }
//
//    @Override
//    public void removeAllTypesOfTasks() {
//        super.removeAllTypesOfTasks();
//        save();
//    }
//
//    @Override
//    public void removeEpicById(int id) {
//        super.removeEpicById(id);
//        save();
//    }
//
//    @Override
//    public void removeSubtaskById(int id) {
//        super.removeSubtaskById(id);
//        save();
//    }
//
//    @Override
//    public void removeTaskById(int id) {
//        super.removeTaskById(id);
//        save();
//    }
//
//    private void save() throws ManagerSaveException {
//        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
//            writer.write("id,type,name,status,description,duration,startTime,epic\n");
//            List<model.Task> uniqueTasks = new ArrayList<>(super.getListOfAllTasks());
//            for (model.Task uniqueTask : uniqueTasks) {
//                String taskToWrite = taskToString2(uniqueTask);
//                writer.write(taskToWrite + "\n");
//            }
//
//            writer.write("\n");
//            writer.write(toString(super.getHistoryManager()));
//
//        } catch (IOException e) {
//            throw new ManagerSaveException("Ошибка при сохранении в файл" + file.getFileName(), e);
//        }
//    }
//
//    //id,type,name,status,description,duration,startTime,epic
//    public model.Task taskFromLoad(String value) {
//        String[] valuesOfFields = value.split(",");
//        int id = Integer.parseInt(valuesOfFields[0]);
//        model.TypesOfTask typesOfTask = model.TypesOfTask.valueOf(valuesOfFields[1]);
//        String title = valuesOfFields[2];
//        model.Task.Status status = model.Task.Status.valueOf(valuesOfFields[3]);
//        String description = valuesOfFields[4];
//
//        Duration duration = null;
//        if (valuesOfFields.length > 6 && !valuesOfFields[5].equals("null")) {
//            duration = Duration.parse(valuesOfFields[5]);
//        }
//
//        LocalDateTime startTime = null;
//        if (valuesOfFields.length > 6 && !valuesOfFields[6].equals("null")) {
//            startTime = LocalDateTime.parse(valuesOfFields[6]);
//        }
//
//        return switch (typesOfTask) {
//            case TASK -> new model.Task(id, title, description, status, duration, startTime);
//            case EPIC -> new model.Epic(id, title, description, status, duration, startTime);
//            case SUBTASK -> {
//                int epicId = Integer.parseInt(valuesOfFields[7]);
//                yield new model.Subtask(id, title, description, status, duration, startTime, epicId);
//            }
//        };
//    }
//
//    public String toString(HistoryManager historyManager) {
//        List<model.Task> taskList = historyManager.getHistory();
//        StringBuilder result = new StringBuilder();
//        for (model.Task task : taskList) {
//            result.append(task.getIdTask()).append(",");
//        }
//        // Удаляем последнюю запятую, если она есть
//        if (!result.isEmpty()) {
//            result.deleteCharAt(result.length() - 1);
//        }
//        return result.toString();
//    }
//
//    @Override
//    public String toString() {
//        return "FileBackendTasksManager{" + "file=" + file + ", historyManager=" + super.getHistoryManager() + '}';
//    }
//
//    @Override
//    public void updateEpic(model.Epic newEpic, int oldId) {
//        super.updateEpic(newEpic, oldId);
//        save();
//    }
//
//    @Override
//    public void updateSubtask(model.Subtask newSubtask, int oldId) {
//        super.updateSubtask(newSubtask, oldId);
//        save();
//    }
//
//    @Override
//    public void updateTask(model.Task newTask, int oldId) {
//        super.updateTask(newTask, oldId);
//        save();
//    }
//
//
//}
//
//
//
