import java.util.*;

public interface TaskManager {
    // -------------------- Создание --------------------

    public void createTask(Task task);
    public void createSubtask(Subtask subtask);
    public void createEpic(Epic epic);


    // -------------------- Получение --------------------

    public Task getTask(int id);
    public List<Task> getTaskList();

    public List<Epic> getEpicList();

    public List<Subtask> getSubtaskList();

    public List<Subtask> getSubtasksOfEpic(Epic epic);

    // -------------------- Обновление --------------------

    public void updateTask(Task newTask, int oldId);

    public void updateEpic(Epic newEpic, int oldId);
    public void updateSubtask(Subtask newSubtask, int oldId);
    // -------------------- Удаление --------------------

    public void deleteAllTasks();

    public void deleteTaskId(int id);

    // -------------------- Вспомогательное --------------------

   // void recalcEpicStatus(Epic epic);
}
