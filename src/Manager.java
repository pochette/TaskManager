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

    public Epic createEpic (String title, String description) {
        Epic epic = new Epic(id++, title, description);
        taskMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask (String title, String description, Epic epic) {
        Subtask subtask = new Subtask(id++, title, description, epic.getId());
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        return subtask;
    }

    public Task getTask (int id) {
        return taskMap.get(id);
    }

    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Task) {
                taskList.add(value);
            }
        }
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Epic) {
                epicList.add((Epic) value);
            }
        }
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Subtask) {
                subtaskList.add( (Subtask) value);
            }
        }
        return subtaskList;
    }

    public void deleteAllTasks () {
        taskMap.clear();
    }

    public void updateTask (int id, Task newtask) {
        taskMap.replace(id, newtask);
    }

    public void deleteTaskId (int id) {
        taskMap.remove(id);
    }
    public List<Integer> getSubtaskOfEpic (Epic epic) {
        List<Integer> subtaskList = new ArrayList<>(epic.getSubtaskList());
        return subtaskList;
    }






}
