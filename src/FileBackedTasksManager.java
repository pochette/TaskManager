import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.SimpleTimeZone;

public class FileBackedTasksManager extends InMemoryTaskManager {

    //TODO дописать метод записи в файл и метод чтения из файла

    private Files file;
    public enum TypesOfTask {
        TASK,
        EPIC,
        SUBTASK
    }


    public String toString(Task task) {
        return super.toString();
    }


    public static String toString(HistoryManager historyManager) {
        return historyManager.toString();
    }


    //TODO метод для восстановления менеджера истории их CSV
    public static List<Integer> fromString(String value) {
        return List<Integer>;
    }

    //TODO метод для сохранения менеджера истории в CSV
    public Task fromString(String value) {

    }

    public FileBackedTasksManager (Files file) {
        super(Manager.getDefaultHistory());
        this.file = file;
    }

    @Override
    public void createTask (Task task) {
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
    public List<Task> getListOfAllTasks() {
        List<Task> taskList = super.getListOfAllTasks();
        save();
        return taskList;
    }

    @Override
    public List<Epic> getEpicList() {
        List<Epic> epicList = super.getEpicList();
        save();
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = super.getSubtaskList();
        save();
        return subtaskList;
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        List<Subtask> subtaskList = super.getSubtasksOfEpic(epic);
        save();
        return subtaskList;
    }

    private void save() {


    }
}
