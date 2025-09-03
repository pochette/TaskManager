import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    private static int id = 0;//Для генерации идентификаторов можно использовать числовое поле класса менеджер, увеличиваия его на
    //нужно получить новое значение

    Map<Integer, Task> taskMap = new HashMap<>();

    public Task createTask (String title, String description) {
        Task task = new Task(id++, title, description);
        taskMap.put(task.getId(), task);
        return task;
    }

    public Task createEpic (String title, String description) {
        Task epic = new Epic(id++, title, description);
        taskMap.put(epic.getId(), epic);
        return epic;
    }

    public Task getTask (int id) {
        return taskMap.get(id);
    }

    public List<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(taskMap.values());
    }

    public void deleteAllTasks () {
        taskMap.clear();
        subtaskMap.clear();
        epicMap.clear();
    }

    public void updateTask (int id, Task newtask) {
        taskMap.replace(id, newtask);
    }

    public void deleteTaskId (int id) {

    }
    public List<Subtask> getSubtaskOfEpic (Epic epic) {

    }





}
