import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {

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
    @AfterEach
    void tearDown() {
        taskManager.deleteAllTasks();
    }

    @Test
    void shouldCreateAndRetrieveTask() {
        taskManager.createTask(task1);
        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getIdTask()));
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test Task", "Test add NewTask description", Task.Status.NEW);
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

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Epic> retrievedEpic = taskManager.getEpicList();
        assertEquals(1, retrievedEpic.size(), "Эпиков должно быть 1");
        assertEquals(retrievedEpic.get(0), taskManager.getTaskById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 1");
        assertEquals(retrievedEpic.get(0), taskManager.getTaskById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 2");
    }

    @Test
    void shouldBe2SubtasksFromEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtaskList = taskManager.getSubtaskList();
        assertEquals(2, subtaskList.size(), "Должно быть 2 сабтаска");
    }

    @Test
    void shouldCalculateEpicStatusCorrectly() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.IN_PROGRESS, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        assertEquals(Task.Status.IN_PROGRESS, taskManager.getTaskById(epic.getIdTask()).getStatus());
    }

    @Test
    void shouldRecalculateEpicStatusToDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask1);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.DONE, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2", Task.Status.DONE, "new description Subtask2", subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи выполнены.");
    }

    @Test
    void recalculateEpicStatusWhenAllSubtaskAreNew() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.NEW, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2", Task.Status.NEW, "new description Subtask2", subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, когда все его подзадачи новые.");
    }

    @Test
    void recalculateEpicStatusForEmptySubtaskList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteTaskById(subtask1.getIdTask());
        taskManager.deleteTaskById(subtask2.getIdTask());

        assertEquals(Task.Status.NEW,epic.getStatus(), "Статус эпика должен быть NEW, когда список подзадач пуст.");

    }
    @Test
    void recalculateEpicStatusWhenAllSubtaskAreDone() {taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.DONE, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2", Task.Status.DONE, "new description Subtask2", subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи завершены.");
    }
    @Test
    void recalculateEpicStatusWhenSubtaskAreNewAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.NEW, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2", Task.Status.DONE, "new description Subtask2", subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
    }

    @Test
    void recalculateEpicStatusWhenSubtaskAreInProgressAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", Task.Status.IN_PROGRESS, "new description Subtask1", subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2", Task.Status.DONE, "new description Subtask2", subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
    }

    @Test
    void shouldReturnSubtaskList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtaskList = taskManager.getSubtaskList();
        assertEquals(2, subtaskList.size(), "Должно быть 2 сабтаска");
    }

    @Test
    void test1_getSubtaskList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtaskList = taskManager.getSubtaskList();
        assertEquals(2, subtaskList.size(), "Должно быть 2 сабтаска");


        taskManager.deleteTaskById(subtask1.getIdTask());
        assertEquals(1, subtaskList.size(), "Должен остаться 1 сабтаск после удаления одного");

        taskManager.deleteAllTasks();
        subtaskList = taskManager.getSubtaskList();
        assertEquals(0, subtaskList.size(), "Список сабтасков должен быть пуст после удаления всех задач");
    }

    @Test
    void test1_getSubtasksOfEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic);
        assertEquals(2, subtasksOfEpic.size(), "Должно быть 2 сабтаска для данного эпика");

        Epic anotherEpic = new Epic("Another Epic", "Another Epic description");
        subtasksOfEpic = taskManager.getSubtasksOfEpic(anotherEpic);
        assertEquals(0, subtasksOfEpic.size(), "Список сабтасков для другого эпика должен быть пуст");

        taskManager.deleteAllTasks();
        subtasksOfEpic = taskManager.getSubtasksOfEpic(epic);
        assertEquals(0, subtasksOfEpic.size(), "Список сабтасков для данного эпика должен быть пуст после удаления всех задач");
    }

}