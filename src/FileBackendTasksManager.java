import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackendTasksManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackendTasksManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.file = path;
        createFileIfNotExist();
        loadFromFile(path);
    }

    public String toString(HistoryManager historyManager) {
        List<Task> taskList = historyManager.getHistory();
        StringBuilder result = new StringBuilder();
        for (Task task : taskList) {
            result.append(task.getIdTask()).append(",");
        }
        // Удаляем последнюю запятую, если она есть
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    private void createFileIfNotExist() throws ManagerSaveException {
        try {
            if (!Files.exists(file)) {
                if (!Files.exists(file.getParent())) {
                    Files.createDirectories(file.getParent());
                }
                Files.createFile(file);
                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    writer.write("id,type,name,status,description,epic\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось создать файл: " + file.toString(), e);
        }
    }

//    public static void main(String[] args) {
//        Path path = Path.of("src/Backend/Backend.csv");
//        FileBackendTasksManager manager = new FileBackendTasksManager(Manager.getDefaultHistory(), path);
//        manager.createTask(new Task("task1", "desc1", Task.Status.NEW));
//        manager.createEpic(new Epic("Epic1", "epic1Description"));
//        manager.createSubtask(new Subtask("Subtask1", "subtask1Description", manager.getEpicList().getLast().getIdTask(), Task.Status.NEW));
//
//        manager.getTaskById(3);
//        manager.getTaskById(2);
//        manager.getTaskById(1);
//
//        System.out.println("--- ИСХОДНЫЙ МЕНЕДЖЕР ---");
//        System.out.println(toString(manager.getHistoryManager()));
//        System.out.println(manager.getListOfAllTasks());
//
//        FileBackendTasksManager managerFromFile = loadFromFile(path);
//
//        System.out.println("\n--- ЗАГРУЖЕННЫЙ МЕНЕДЖЕР ---");
//        System.out.println(toString(managerFromFile.getHistoryManager()));
//        System.out.println(managerFromFile.getListOfAllTasks());
//
//        System.out.println("\n--- ПРОВЕРКА ---");
//        System.out.println("Задачи идентичны: " + manager.getListOfAllTasks().equals(managerFromFile.getListOfAllTasks()));
//        System.out.println("История идентична: " +
//                toString(manager.getHistoryManager()).equals(toString(managerFromFile.getHistoryManager()))
//        );
//    }

    // TODO дописать метод восстановления из файла, taskmap теперь закрыта, доступ только через create методы.
    // дописать логику восстановления связей эпиков и сабтасков

    public void loadFromFile(Path path) throws ManagerSaveException {
        if (!Files.exists(path)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (String line : lines.stream().skip(1).collect(Collectors.toList())) {
                Task task = taskFromLoad(line);
                switch (task.getType()) {
                    case TASK:
                        super.createTask(task);
                        break;
                    case EPIC:
                        super.createEpic((Epic) task);
                        break;
                    case SUBTASK:
                        super.createSubtask((Subtask) task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось загрузить данные из файла", e);
        }
    }

    public static Task taskFromLoad(String value) {
        String[] valuesOfFields = value.split(",");
        Integer id = Integer.parseInt(valuesOfFields[0]);
        TypesOfTask typesOfTask = TypesOfTask.valueOf(valuesOfFields[1]);
        String title = valuesOfFields[2];
        Task.Status status = Task.Status.valueOf(valuesOfFields[3]);
        String description = valuesOfFields[4];

        return switch (typesOfTask) {
            case TASK -> new Task(id, title, description, status );
            case EPIC -> new Epic(id, title, status, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(valuesOfFields[5]);
                yield new Subtask(id, title,  description, status, epicId);
            }
        };
    }


    private void save() throws ManagerSaveException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTaskMap().values()) {
                writer.write(taskToString(task) + "\n");
            }

            writer.write("\n");
            writer.write(toString(super.getHistoryManager()));

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
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
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

    private String taskToString(Task task) {
        String epicIdField = "";
        if (task instanceof Subtask) {
            epicIdField = String.valueOf(((Subtask) task).getEpicIdTask());
        }
        return String.join(",",
                String.valueOf(task.getIdTask()),
                task.getType().toString(),
                task.getTitle(),
                task.getStatus().toString(),
                task.getDescription(),
                epicIdField
        );
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Override
    public String toString() {
        return "FileBackendTasksManager{" +
                "file=" + file +
                ", historyManager=" + super.getHistoryManager() +
                '}';
    }

}

