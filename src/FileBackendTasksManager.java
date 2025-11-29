import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackendTasksManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackendTasksManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.file = path;
    }

    public static String toString (HistoryManager historyManager) {
        List <Task> taskList = historyManager.getHistory();
        StringBuilder result = new StringBuilder();
        for (Task task : taskList) {
            result.append(task.getId()).append(",");
        }
        // Удаляем последнюю запятую, если она есть
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Path path = Path.of("C:/Users/burda/IdeaProjects/TaskManager/src/Backend/Backend.csv");
        FileBackendTasksManager manager = new FileBackendTasksManager(Manager.getDefaultHistory(), path);
        manager.createTask(new Task("task1", "desc1", Task.Status.NEW));
        manager.createEpic(new Epic("Epic1", "epic1Description"));
        manager.createSubtask(new Subtask("Subtask1", "subtask1Description", 2, Task.Status.NEW));

        manager.getTask(3);
        manager.getTask(2);
        manager.getTask(1);

        System.out.println("--- ИСХОДНЫЙ МЕНЕДЖЕР ---");
        System.out.println(toString(manager.getHistoryManager()));
        System.out.println(manager.getListOfAllTasks());

        FileBackendTasksManager managerFromFile = loadFromFile(path);

        System.out.println("\n--- ЗАГРУЖЕННЫЙ МЕНЕДЖЕР ---");
        System.out.println(toString(managerFromFile.getHistoryManager()));
        System.out.println(managerFromFile.getListOfAllTasks());

        System.out.println("\n--- ПРОВЕРКА ---");
        System.out.println("Задачи идентичны: " + manager.getListOfAllTasks().equals(managerFromFile.getListOfAllTasks()));
        System.out.println("История идентична: " +
                toString(manager.getHistoryManager()).equals(toString(managerFromFile.getHistoryManager()))
        );
    }

    @Override
    public String toString() {
        return "FileBackendTasksManager{" +
                "file=" + file +
                ", historyManager=" + historyManager +
                '}';
    }

    public static FileBackendTasksManager loadFromFile(Path path) throws ManagerSaveException {
        if (!Files.exists(path)) {
            throw new ManagerSaveException("Файл не найден", null);
        }
        FileBackendTasksManager manager = new FileBackendTasksManager(new InMemoryHistoryManager(), path);
        int maxId = 0;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            boolean isHistorySection = false;

            for (String line : lines) {
                if (line.isBlank()) {
                    isHistorySection = true;
                    continue;
                }
                if (line.equals("id,type,name,status,description,epic")) {
                    continue;
                }

                if (!isHistorySection) {
                    Task task = formString(line);
                    manager.taskMap.put(task.getId(), task);
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                } else {
                    String[] ids = line.split(",");
                    for (String idStr : ids) {
                        manager.historyManager.add(manager.taskMap.get(Integer.parseInt(idStr)));
                    }
                }
            }

            // Восстанавливаем связи
            for (Task task : manager.taskMap.values()) {
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Epic epic = (Epic) manager.taskMap.get(subtask.getEpicId());
                    if (epic != null) {
                        epic.addSubtask(subtask.getId());
                    }
                }
            }
            manager.id = maxId + 1; // Устанавливаем следующий ID

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось загрузить данные из файла",e);
        }
        return manager;
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.toFile(), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : taskMap.values()) {
                writer.write(taskToString(task) + "\n");
            }

            writer.write("\n");
            writer.write(toString(historyManager));

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

    public static Task formString(String value) {
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

    private String taskToString(Task task) {
        String epicIdField = "";
        if (task instanceof Subtask) {
            epicIdField = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.join(",",
                String.valueOf(task.getId()),
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

}
