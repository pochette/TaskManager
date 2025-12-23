import java.util.List;

public interface TaskManager {
    // -------------------- Создание --------------------

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    HistoryManager getHistoryManager();

    // -------------------- Получение --------------------

    Task getTaskById(int id);

    List<Task> getListOfAllTasks();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    List<Subtask> getSubtasksOfEpic(Epic epic);

    // -------------------- Обновление --------------------

    void updateTask(Task newTask, int oldId);

    void updateEpic(Epic newEpic, int oldId);

    void updateSubtask(Subtask newSubtask, int oldId);
    // -------------------- Удаление --------------------

    void deleteAllTasks();

    void deleteTaskById(int id);

    // -------------------- Вспомогательное --------------------

    // void recalcEpicStatus(Epic epic);
}
