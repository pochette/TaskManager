import java.util.*;

public interface TaskManager {
    // -------------------- Создание --------------------

    public Task createTask(String title, String description);

    public Task createTask(String title, String description, Task.Status status);

    public Epic createEpic(String title, String description);

    public Subtask createSubtask(String title, String description, Epic epic);
    public Subtask createSubtask(String title, String description, Epic epic, Task.Status status);

    // -------------------- Получение --------------------

    public Task getTask(int id);
    public List<Task> getTaskList();

    public List<Epic> getEpicList();

    public List<Subtask> getSubtaskList();

    public List<Subtask> getSubtasksOfEpic(Epic epic);

    // -------------------- Обновление --------------------

    public void updateTask(Task newTask);

    public void updateEpic(Epic newEpic);
    public void updateSubtask(Subtask newSubtask);
    // -------------------- Удаление --------------------

    public void deleteAllTasks();

    public void deleteTaskId(int id);

    // -------------------- Вспомогательное --------------------

   // void recalcEpicStatus(Epic epic);
}
