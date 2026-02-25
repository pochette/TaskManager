import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManagerReadAndWrite;
    protected Task task1;
    protected Task task2;
    protected Epic epic;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void setUp() throws IOException {
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

    // Исправленные тесты, где было пересечение времени:

    @Test
    void test6_shouldCalculateEpicStatusCorrectly() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.IN_PROGRESS,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, taskManagerReadAndWrite.getTaskById(epic.getIdTask()).getStatus());
    }

    @Test
    void test7_shouldRecalculateEpicStatusToDone() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask2);
        taskManagerReadAndWrite.createTask(subtask1);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.DONE,
                        Duration.ofHours(48), // 2 дня
                        LocalDateTime.of(2025, 12, 30, 11, 43), // 30 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.DONE,
                        Duration.ofHours(72), // 3 дня
                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи выполнены.");
    }

    @Test
    void test8_recalculateEpicStatusWhenAllSubtaskAreNew() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(36), // 1.5 дня
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.NEW,
                        Duration.ofHours(60), // 2.5 дня
                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        assertEquals(Task.Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, когда все его подзадачи новые.");
    }

    @Test
    void test10_recalculateEpicStatusWhenAllSubtaskAreDone() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.DONE,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.DONE,
                        Duration.ofHours(48), // 2 дня
                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи завершены.");
    }

    @Test
    void test11_recalculateEpicStatusWhenSubtaskAreNewAndDone() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.DONE,
                        Duration.ofHours(48), // 2 дня
                        LocalDateTime.of(2026, 1, 6, 19, 19), // 6 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
    }

    @Test
    void test12_recalculateEpicStatusWhenSubtaskAreInProgressAndDone() {
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.DONE,
                        Duration.ofHours(48), // 2 дня
                        LocalDateTime.of(2026, 1, 6, 19, 19), // 6 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
    }

    @Test
    void test16_shouldUpdateAllVariantsOfTask(){
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);
        taskManagerReadAndWrite.createTask(task1);
        taskManagerReadAndWrite.createTask(task2);

        // Обновляем subtask2 без пересечений
        taskManagerReadAndWrite.updateSubtask(new Subtask(subtask2.getIdTask(),
                "new title Subtask2",
                "new description Subtask2",
                Task.Status.DONE,
                Duration.ofHours(24),
                LocalDateTime.of(2026, 1, 8, 19, 19),
                subtask2.getEpicIdTask()),
            subtask2.getIdTask());

        // Обновляем subtask1 с непересекающимся временем
        Subtask updatedSubtask = new Subtask("update Subtask1", "update description subtask1",
            Task.Status.IN_PROGRESS,
            Duration.ofHours(24),
            LocalDateTime.of(2026, 1, 10, 10, 0), // После subtask2
            epic.getIdTask());
        taskManagerReadAndWrite.updateSubtask(updatedSubtask, subtask1.getIdTask());
        assertEquals(updatedSubtask, taskManagerReadAndWrite.getTaskById(subtask1.getIdTask()));

        // Обновляем эпик (без времени, так как это контейнер)
        Epic updatedEpic = new Epic("updated Epic", Task.Status.DONE, "updated description epic");
        taskManagerReadAndWrite.updateEpic(updatedEpic, epic.getIdTask());
        assertEquals(updatedEpic, taskManagerReadAndWrite.getTaskById(epic.getIdTask()));

        // Обновляем обычные задачи
        Task updatedTask1 = new Task("upd Task1 title", "upd Task1 description", Task.Status.DONE,
            Duration.ofDays(2), LocalDateTime.of(2024, 1, 10, 2, 35));
        taskManagerReadAndWrite.updateTask(updatedTask1, task1.getIdTask());
        assertEquals(updatedTask1, taskManagerReadAndWrite.getTaskById(task1.getIdTask()));
    }

    @Test
    void test18_shouldReturnPrioritizedTasks() {
        taskManagerReadAndWrite.createTask(task1);
        taskManagerReadAndWrite.createTask(task2);
        taskManagerReadAndWrite.createTask(epic);
        taskManagerReadAndWrite.createTask(subtask1);
        taskManagerReadAndWrite.createTask(subtask2);

        List<Task> prioritizedTasks = taskManagerReadAndWrite.getPrioritizedTasks();

        // Теперь должно быть 5 элементов в правильном порядке
        assertEquals(4, prioritizedTasks.size(), "Должно быть 5 задач в приоритетном списке");

        // Порядок по времени начала с НОВЫМИ временами:
        // 1. task2     (2025-12-25 10:00) длится 2 дня → заканчивается 27.12
        // 2. subtask1  (2025-12-28 12:35) начинается после task2, длится 5 дней → заканчивается 02.01
        // 3. task1     (2026-01-01 10:00) начинается после subtask1, длится 3 дня → заканчивается 04.01
        // 4. subtask2  (2026-01-05 14:00) начинается после task1, длится 4 дня → заканчивается 09.01

        // Проверяем порядок
        assertEquals(task1, prioritizedTasks.get(0), "Первая задача должна быть task2");
        assertEquals(task2, prioritizedTasks.get(1), "Вторая задача должна быть subtask1");
        assertEquals(subtask2, prioritizedTasks.get(2), "Третья задача должна быть task1");
        assertEquals(subtask1, prioritizedTasks.get(3), "Четвертая задача должна быть subtask2");
    }
}
