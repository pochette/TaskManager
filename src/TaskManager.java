import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    Task getTaskById(int id);

    List<Task> getListOfAllTasks();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    List<Subtask> getSubtasksOfEpic(int epicId);

    void updateTask(Task newTask, int oldId);

    void updateEpic(Epic newEpic, int oldId);

    void updateSubtask(Subtask newSubtask, int oldId);

    void recalcEpicStatus (Epic epic);

    void removeAllTasks();

    void removeTaskById(int id);

    void removeAllEpics();

    void removeEpicById(int id);

    void removeAllSubtasks();

    void removeSubtaskById(int id);

    List<Task> getHistory();





}
