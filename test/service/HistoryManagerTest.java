import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {
    private final TaskManager taskManager = Manager.getDefault();
    private Task task1;
    private Task task2;
    private Epic epic;

    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void setUp() {
        task1 = new Task("Task1 title", "Task1 Description", Task.Status.NEW,
                Duration.ofDays(15), LocalDateTime.of(2020, 1, 1, 10, 0));
        task2 = new Task("Task2 title", "Task2 Description", Task.Status.NEW,
                Duration.ofDays(10), LocalDateTime.of(2021, 12, 25, 10, 0));

        epic = new Epic("Epic title", Task.Status.NEW, "Epic Description");

        subtask1 = new Subtask(
                "Subtask1 title",
                "Subtask1 description",

                Task.Status.NEW,
                Duration.ofDays(30),
                LocalDateTime.of(2030, 12, 25, 12, 35), epic.getIdTask());
        subtask2 = new Subtask(
                "Subtask2 title",
                "Subtask2 description",
                Task.Status.NEW,
                Duration.ofDays(20),
                LocalDateTime.of(2024, 1, 15, 14, 0), epic.getIdTask());
    }

    @AfterEach
    void tearDown() {
        taskManager.removeAllTypesOfTasks();
    }

    @Test
    void test1_shouldDeleteHistoryWhenDeleteTaskById() {
        taskManager.createTask(epic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getTaskById(subtask1.getIdTask());
        taskManager.getTaskById(epic.getIdTask());

        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");

        taskManager.removeTaskById(task1.getIdTask());
        assertEquals(2, taskManager.getHistory().size(), "В истории должно быть 2 задачи после удаления задачи 1");

        taskManager.removeTaskById(subtask1.getIdTask());
        assertEquals(1, taskManager.getHistory().size(), "В истории должна быть 1 задача после удаления подзадачи 1");

        taskManager.removeTaskById(epic.getIdTask());
        assertEquals(0, taskManager.getHistory().size(), "История должна быть пустой после удаления эпика");
    }

    @Test
    void test2_shouldNotAddDuplicateTasksInHistory() {
        taskManager.createTask(epic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        taskManager.getTaskById(task1.getIdTask()); // Повторное добавление задачи 1
        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 уникальные задачи без дубликатов");
    }

    @Test
    void test3_shouldRemoveFromCenterOfHistory() {
        taskManager.createTask(epic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");
        taskManager.removeTaskById(subtask1.getIdTask());
        assertEquals(2, taskManager.getHistory().size(), "В истории должно быть 2 задачи после удаления подзадачи из центра истории");
        assertEquals(task1, taskManager.getHistory().get(0), "Первая задача в истории должна быть task1");
        assertEquals(epic, taskManager.getHistory().get(1), "Вторая задача в истории должна быть epic");
    }

    @Test
    void test4_shouldHandleEmptyHistory() {
        assertEquals(0, taskManager.getHistory().size(), "История должна быть пустой при отсутствии добавленных задач");
    }

    @Test
    void test5_shouldRemoveTaskFromBeginningOfHistory() {
        taskManager.createTask(epic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");
        taskManager.removeTaskById(task1.getIdTask());
        assertEquals(2, taskManager.getHistory().size(), "В истории должно быть 2 задачи после удаления задачи из начала истории");
        assertEquals(subtask1, taskManager.getHistory().get(0), "Первая задача в истории должна быть subtask1");
        assertEquals(epic, taskManager.getHistory().get(1), "Вторая задача в истории должна быть epic");
    }

    @Test
    void test6_shouldRemoveTaskFromEndOfHistory() {
        taskManager.createTask(epic);
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getIdTask());
        taskManager.getSubtaskById(subtask1.getIdTask());
        taskManager.getEpicById(epic.getIdTask());
        assertEquals(3, taskManager.getHistory().size(), "В истории должно быть 3 задачи");
        taskManager.removeTaskById(epic.getIdTask());
        assertEquals(1, taskManager.getHistory().size(), "В истории должна быть 1 задачи после удаления эпика из конца истории");
        assertEquals(task1, taskManager.getHistory().get(0), "Первая задача в истории должна быть task1");
    }
}







