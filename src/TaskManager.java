import java.util.List;

public interface TaskManager {

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void createTask(Task task);

    Epic getEpicById(int id);

    List<Epic> getEpicList();

    List<Task> getHistory();

    List<Task> getListOfAllTasks();

    List<Task> getPrioritizedTasks();

    Subtask getSubtaskById(int id);

    List<Subtask> getSubtaskList();

    List<Subtask> getSubtasksOfEpic(int epicId);

    Task getTaskById(int id);

    void recalcEpicStatus(Epic epic);

    void removeAllEpics();

    void removeAllSubtasks();

    void removeAllTasks();

    void removeAllTypesOfTasks();

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    void removeTaskById(int id);

    void updateEpic(Epic newEpic, int oldId);

    void updateSubtask(Subtask newSubtask, int oldId);

    void updateTask(Task newTask, int oldId);


}
