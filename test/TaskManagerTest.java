import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {


    protected T taskManager;
    protected Task task1;
    protected Task task2;

    protected Epic epic;

    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void setUp() throws IOException {

        task1 = new Task("Task1 title", "Task1 description");
        task2 = new Task("Task2 title", "Task2 description");

        epic = new Epic("Epic title", "Epic description");

        subtask1 = new Subtask("Subtask1 title", "Subtask1 description", epic.getIdTask());
        subtask2 = new Subtask("Subtask2 title", "Subtask2 description", epic.getIdTask());

    }

    @Test
    void shouldCreateAndRetrieveTask() {
        taskManager.createTask(task1);
        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getIdTask()));
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test Task", "Test add NewTask desctription", Task.Status.NEW);
        taskManager.createTask(task);
        final int taskId = task.getIdTask();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задача не найдена");

        final List<Task> tasks = taskManager.getListOfAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
   void shouldReturnEpicFromSubtask() {

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createEpic(epic);


        List<Epic> retrievedEpic = taskManager.getEpicList();
        assertEquals(1, retrievedEpic.size(), "Эпиков должно быть 1");
        assertEquals(retrievedEpic.get(0), taskManager.getTaskById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 1");
        assertEquals(retrievedEpic.get(0), taskManager.getTaskById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 2");
    }




}