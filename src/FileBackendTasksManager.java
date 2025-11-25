import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackendTasksManager extends InMemoryTaskManager {
    // private final Path path = Paths.get("C:/Users/burda/IdeaProjects/TaskManager/src/Backend/Backend.csv");
    private final Path file;

    public FileBackendTasksManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.file = path;
    }

    public static List<Integer> fromString(String value) {
        List<Integer> listOfIdTask = new ArrayList<>();
        for (String s : value.split(",")) {
            listOfIdTask.add(Integer.parseInt(s));
        }
        return listOfIdTask;
    }

    public static String toString (HistoryManager historyManager) {
        List <Task> taskList = historyManager.getHistory();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            result.append(taskList.get(i).getId());
            if (i < taskList.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Path path = Paths.get("C:/Users/burda/IdeaProjects/TaskManager/src/Backend/Backend.csv");
        FileBackendTasksManager manager = new FileBackendTasksManager(Manager.getDefaultHistory(), path);
        manager.createTask(new Task("task1", "desc1", Task.Status.NEW));
        manager.createEpic(new Epic("Epic1", "epic1Description"));
        manager.createSubtask(new Subtask("Subtask1", "subtask1Description", 2, Task.Status.NEW));

        manager.getTask(1);
        manager.getTask(3);

    }

    //TODO дописать чтение из файла

//TODO дописать метод записи в файл и метод чтения из файла

//todo дописать метод loadFromFile
//    public static FileBackendTasksManager loadFromFile(File file) {
//        return new FileBackendTasksManager(file);
//    }

// метод восстановления менеджера истории из CSV

    private void save() throws ManagerSaveException {

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : taskMap.values()) {
                if (!(task instanceof Subtask)) {
                    writer.write(task.toString(task) + "\n");
                } else {
                    Subtask subtask = (Subtask) task;
                    writer.write(subtask.toString(subtask) + "\n");
                }
            }

            writer.write("\n");

            for (Task task : historyManager.getHistory()) {
                writer.write(task.getId() + ",");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл" + file.getFileName(), e);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();

    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void updateTask(Task newTask, int oldId) {
        super.updateTask(newTask, oldId);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic, int oldId) {
        super.updateEpic(newEpic, oldId);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask, int oldId) {
        super.updateSubtask(newSubtask, oldId);
        save();
    }

    public Task formString(String value) {
        String[] valuesOfFields = value.split(",");
        Integer id = Integer.parseInt(valuesOfFields[0]);
        TypesOfTask typesOfTask = TypesOfTask.valueOf(valuesOfFields[1]);
        String title = valuesOfFields[2];
        Task.Status status = Task.Status.valueOf(valuesOfFields[3]);
        String description = valuesOfFields[4];

        return switch (typesOfTask) {
            case TASK -> new Task(id, title, status, description);
            case EPIC -> new Epic(id, title, status, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(valuesOfFields[5]);
                yield new Subtask(id, title, status, description, epicId);
            }
        };
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}

