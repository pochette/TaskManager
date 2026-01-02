import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;

    protected Epic epic;

    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void setUp() throws IOException {

        task1 = new Task("Task1 title", "Task1 Description", TypesOfTask.TASK, Task.Status.NEW,
                Duration.ofDays(15), LocalDateTime.of(2026, 1, 1, 10, 0));
        task2 = new Task( "Task2 title", "Task2 Description", TypesOfTask.TASK, Task.Status.NEW,
                Duration.ofDays(10), LocalDateTime.of(2025, 12, 25, 10, 0));

        epic = new Epic( "Epic title", "Epic description");

        subtask1 = new Subtask(
                                "Subtask1 title",
                                "Subtask1 description",
                TypesOfTask.SUBTASK,
                Task.Status.NEW,
                Duration.ofDays(30),
                LocalDateTime.of(2025, 12, 25,12,35), epic.getIdTask());
        subtask2 = new Subtask(
                                "Subtask2 title",
                                "Subtask2 description",
                TypesOfTask.SUBTASK,
                Task.Status.NEW,
                Duration.ofDays(20),
                LocalDateTime.of(2026, 1, 15,14,0), epic.getIdTask());
    }
//    @AfterEach
//    void tearDown() {
//        taskManager.removeAllTypesOfTasks();
//    }


    @Test
    void shouldCreateAndRetrieveTask() {
        taskManager.createTask(task1);
        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getIdTask()));
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test Task", "Test add NewTask description", Task.Status.NEW, TypesOfTask.TASK);
        taskManager.createTask(task);
        final int taskId = task.getIdTask();
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задача не найдена");

        final List<Task> tasks = taskManager.getListOfAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateAndDeleteTaskAndListOfTasks() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        assertTrue(taskManager.getListOfAllTasks().contains(task1), "Задача 1 не найдена в списке всех задач");
        assertTrue(taskManager.getListOfAllTasks().contains(epic), "Эпик не найден в списке всех задач");
        assertTrue(taskManager.getListOfAllTasks().contains(subtask1), "Подзадача 1 не найдена в списке всех задач");

        assertTrue(taskManager.getTaskList().contains(task1), "Задача 1 не найдена в списке всех задач");
        assertTrue(taskManager.getEpicList().contains(epic), "Эпик не найден в списке всех задач");
        assertTrue(taskManager.getSubtaskList().contains(subtask1), "Подзадача 1 не найдена в списке всех задач");

        taskManager.removeTaskById(task1.getIdTask());
        assertNull(taskManager.getTaskById(task1.getIdTask()));

        taskManager.removeEpicById(epic.getIdTask());
        assertNull(taskManager.getEpicById(epic.getIdTask()));


        assertNull(taskManager.getSubtaskById(subtask1.getIdTask()), "Подзадача должна быть удалена вместе с эпиком");

        List<Task> allTasks = taskManager.getListOfAllTasks();
        assertEquals(allTasks, taskManager.getListOfAllTasks(), "Списки всех задач не совпадают");



    }

    @Test
    void shouldReturnEpicFromSubtask() {

        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Epic> retrievedEpic = taskManager.getEpicList();
        assertEquals(1, retrievedEpic.size(), "Эпиков должно быть 1");
        assertEquals(retrievedEpic.getFirst(), taskManager.getEpicById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 1");
        assertEquals(retrievedEpic.getFirst(), taskManager.getEpicById(subtask2.getEpicIdTask()), "Эпик не совпадает с сабтаском 2");
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

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1",  "new description Subtask1", Task.Status.IN_PROGRESS, subtask1.getEpicIdTask()), subtask1.getIdTask());
        assertEquals(Task.Status.IN_PROGRESS, taskManager.getEpicById(epic.getIdTask()).getStatus());
    }

    @Test
    void shouldRecalculateEpicStatusToDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask1);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", "new description Subtask1", Task.Status.DONE, subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2",  "new description Subtask2", Task.Status.DONE, subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи выполнены.");
    }

    @Test
    void recalculateEpicStatusWhenAllSubtaskAreNew() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1",  "new description Subtask1", Task.Status.NEW, subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2",  "new description Subtask2", Task.Status.NEW, subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, когда все его подзадачи новые.");
    }

    @Test
    void recalculateEpicStatusForEmptySubtaskList() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.removeSubtaskById(subtask1.getIdTask());
        taskManager.removeSubtaskById(subtask2.getIdTask());

        assertEquals(Task.Status.NEW,epic.getStatus(), "Статус эпика должен быть NEW, когда список подзадач пуст.");

    }
    @Test
    void recalculateEpicStatusWhenAllSubtaskAreDone() {taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1", "new description Subtask1",  Task.Status.DONE, subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2",  "new description Subtask2", Task.Status.DONE, subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи завершены.");
    }
    @Test
    void recalculateEpicStatusWhenSubtaskAreNewAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1",  "new description Subtask1", Task.Status.NEW, subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2",  "new description Subtask2", Task.Status.DONE, subtask2.getEpicIdTask()), subtask2.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
    }

    @Test
    void recalculateEpicStatusWhenSubtaskAreInProgressAndDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(), "new title Subtask1",  "new description Subtask1", Task.Status.IN_PROGRESS, subtask1.getEpicIdTask()), subtask1.getIdTask());
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(), "new title Subtask2",  "new description Subtask2", Task.Status.DONE, subtask2.getEpicIdTask()), subtask2.getIdTask());

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


        taskManager.removeSubtaskById(subtask1.getIdTask());
        subtaskList = taskManager.getSubtaskList();
        assertEquals(1, subtaskList.size(), "Должен остаться 1 сабтаск после удаления одного");

        taskManager.removeAllTypesOfTasks();
        subtaskList = taskManager.getSubtaskList();
        assertEquals(0, subtaskList.size(), "Список сабтасков должен быть пуст после удаления всех задач");
    }

    @Test
    void test1_getSubtasksOfEpic() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic.getIdTask());
        assertEquals(2, subtasksOfEpic.size(), "Должно быть 2 сабтаска для данного эпика");

        Epic anotherEpic = new Epic("Another Epic", "Another Epic description");
        taskManager.createEpic(anotherEpic);
        subtasksOfEpic = taskManager.getSubtasksOfEpic(anotherEpic.getIdTask());
        assertEquals(0, subtasksOfEpic.size(), "Список сабтасков для другого эпика должен быть пуст");

        taskManager.removeAllTypesOfTasks();
        subtasksOfEpic = taskManager.getSubtasksOfEpic(epic.getIdTask());
        assertEquals(0, subtasksOfEpic.size(), "Список сабтасков для данного эпика должен быть пуст после удаления всех задач");
    }


    @Test
    void shouldUpdateAllVariantsOfTask (){
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Subtask updatetedSubtask = new Subtask("update Subtask1", "update description subtask1",
                Task.Status.IN_PROGRESS, epic.getIdTask());
        taskManager.updateSubtask(updatetedSubtask, subtask1.getIdTask());

        assertEquals(updatetedSubtask, taskManager.getSubtaskById(subtask1.getIdTask()));

        Subtask updatedSubtask2 = new Subtask("update Subtask2", "update description subtask2",
                Task.Status.DONE, epic.getIdTask());
        taskManager.updateSubtask(updatedSubtask2,subtask1.getIdTask());
        assertEquals(updatedSubtask2, taskManager.getSubtaskById(subtask1.getIdTask()));

        Epic updatedEpic = new Epic("updated Epic", "updated description epic");
        taskManager.updateEpic(updatedEpic, epic.getIdTask());
        assertEquals(updatedEpic, taskManager.getEpicById(epic.getIdTask()));

        Task updatedTask1 = new Task("updated Task1", "updated description task1");
        taskManager.updateTask(updatedTask1, task1.getIdTask());
        assertEquals(updatedTask1, taskManager.getTaskById(task1.getIdTask()));


    }

    @Test
    void shouldDeleteTaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeTaskById(task1.getIdTask());
        assertNull(taskManager.getTaskById(task1.getIdTask()), "Задача 1 должна быть удалена");

        taskManager.removeSubtaskById(subtask1.getIdTask());
        assertNull(taskManager.getSubtaskById(subtask1.getIdTask()), "Подзадача 1 должна быть удалена");

        taskManager.removeEpicById(epic.getIdTask());
        assertNull(taskManager.getEpicById(epic.getIdTask()), "Эпик) должен быть удален");
        assertNull(taskManager.getSubtaskById(subtask1.getIdTask()), "Подзадача 1 должна быть удалена вместе с эпиком");
        assertNull(taskManager.getSubtaskById(subtask2.getIdTask()), "Подзадача 2 должна быть удалена вместе с эпиком");
    }
    @Test
    void shouldDeleteHistoryWhenDeleteTaskById() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());

        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");

        taskManager.removeTaskById(task1.getIdTask());
        assertEquals(2, taskManager.getHistory().size(), "В истории должно быть 2 задачи после удаления задачи 1");

        taskManager.removeSubtaskById(subtask1.getIdTask());
        assertEquals(1, taskManager.getHistory().size(), "В истории должна быть 1 задача после удаления подзадачи 1");

        taskManager.removeEpicById(epic.getIdTask());
        assertEquals(0, taskManager.getHistory().size(), "История должна быть пустой после удаления эпика");
    }
}