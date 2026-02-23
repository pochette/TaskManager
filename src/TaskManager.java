import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    List<Epic> getEpicList();

    List<Task> getHistory();

    List<Task> getListOfAllTasks();

    List<Task> getPrioritizedTasks();

    List<Subtask> getSubtaskList();

    List<Subtask> getSubtasksOfEpic(int epicId);

    Task getTaskById(int id);

    List<Task> getTaskList();

    void removeAllEpics();

    void removeAllOrdinaryTasks();

    void removeAllSubtasks();

    void removeAllTypesOfTasks();

    void removeTaskById(int id);

    void updateEpic(Epic newEpic, int oldId);

    void updateSubtask(Subtask newSubtask, int oldId);

    void updateTask(Task newTask, int oldId);

}
