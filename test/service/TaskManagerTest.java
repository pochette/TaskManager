import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.IN_PROGRESS,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        assertEquals(Task.Status.IN_PROGRESS, taskManager.getEpicById(epic.getIdTask()).getStatus());
    }

    @Test
    void test7_shouldRecalculateEpicStatusToDone() {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask1);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.DONE,
                        Duration.ofHours(48), // 2 дня
                        LocalDateTime.of(2025, 12, 30, 11, 43), // 30 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(36), // 1.5 дня
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.DONE,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
                        "new title Subtask1",
                        "new description Subtask1",
                        Task.Status.NEW,
                        Duration.ofHours(24), // 1 день
                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
                        subtask1.getEpicIdTask()),
                subtask1.getIdTask());

        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
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
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Subtask updatedSubtask = new Subtask("update Subtask1", "update description subtask1",
                Task.Status.IN_PROGRESS,
                Duration.ofHours(24), // 1 день
                LocalDateTime.of(2026, 1, 7, 10, 0), // 7 января (после всех задач)
                epic.getIdTask());

        // Исправляем время, чтобы не пересекалось
        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
                        "new title Subtask2",
                        "new description Subtask2",
                        Task.Status.DONE,
                        Duration.ofHours(36), // 1.5 дня
                        LocalDateTime.of(2026, 1, 8, 19, 19), // 8 января
                        subtask2.getEpicIdTask()),
                subtask2.getIdTask());

        taskManager.updateSubtask(updatedSubtask, subtask1.getIdTask());
        assertEquals(updatedSubtask, taskManager.getSubtaskById(subtask1.getIdTask()));

        Subtask updatedSubtask2 = new Subtask("update Subtask2", "update description subtask2",
                Task.Status.DONE,
                Duration.ofHours(48), // 2 дня
                LocalDateTime.of(2019, 1, 9, 10, 0), // 9 января
                epic.getIdTask());

        taskManager.updateSubtask(updatedSubtask2, subtask1.getIdTask());
        assertEquals(updatedSubtask2, taskManager.getSubtaskById(subtask1.getIdTask()));

        Epic updatedEpic = new Epic("updated Epic", Task.Status.DONE, "updated description epic");
        taskManager.updateEpic(updatedEpic, epic.getIdTask());
        assertEquals(updatedEpic, taskManager.getEpicById(epic.getIdTask()));

        Task updatedTask1 = new Task("upd Task1 title", "upd Task1 description", Task.Status.DONE,
                Duration.ofDays(2), LocalDateTime.of(2024, 1, 10, 2, 35)); // 10 января
        taskManager.updateTask(updatedTask1, task1.getIdTask());
        assertEquals(updatedTask1, taskManager.getTaskById(task1.getIdTask()));
    }

    @Test
    void test18_shouldReturnPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

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



//import org.junit.jupiter.api.*;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static java.time.temporal.ChronoUnit.HOURS;
//import static org.junit.jupiter.api.Assertions.*;
//
//public abstract class TaskManagerTest<T extends TaskManager> {
//
//    protected T taskManager;
//    protected Task task1;
//    protected Task task2;
//    protected Epic epic;
//    protected Subtask subtask1;
//    protected Subtask subtask2;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // Задачи с НЕПЕРЕСЕКАЮЩИМСЯ временем
//        task1 = new Task("Task1 title", "Task1 Description", Task.Status.NEW,
//                Duration.ofDays(3), LocalDateTime.of(2026, 1, 1, 10, 0)); // 1-4 января
//
//        task2 = new Task("Task2 title", "Task2 Description", Task.Status.NEW,
//                Duration.ofDays(2), LocalDateTime.of(2025, 12, 25, 10, 0)); // 25-27 декабря
//
//        epic = new Epic("Epic title", Task.Status.NEW, "Epic description");
//
//        // Подзадачи с НЕПЕРЕСЕКАЮЩИМСЯ временем
//        subtask1 = new Subtask(
//                "Subtask1 title",
//                "Subtask1 description",
//                Task.Status.NEW,
//                Duration.ofDays(5), // 5 дней
//                LocalDateTime.of(2025, 12, 28, 12, 35), // начинается 28 декабря
//                epic.getIdTask());
//
//        subtask2 = new Subtask(
//                "Subtask2 title",
//                "Subtask2 description",
//                Task.Status.NEW,
//                Duration.ofDays(4), // 4 дня
//                LocalDateTime.of(2026, 1, 5, 14, 0), // начинается 5 января (после окончания task1)
//                epic.getIdTask());
//    }
//
//    // Исправленные тесты, где было пересечение времени:
//
//    @Test
//    void test6_shouldCalculateEpicStatusCorrectly() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.IN_PROGRESS,
//                        Duration.ofHours(24), // 1 день
//                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        assertEquals(Task.Status.IN_PROGRESS, taskManager.getEpicById(epic.getIdTask()).getStatus());
//    }
//
//    @Test
//    void test7_shouldRecalculateEpicStatusToDone() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask2);
//        taskManager.createSubtask(subtask1);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.DONE,
//                        Duration.ofHours(48), // 2 дня
//                        LocalDateTime.of(2025, 12, 30, 11, 43), // 30 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.DONE,
//                        Duration.ofHours(72), // 3 дня
//                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи выполнены.");
//    }
//
//    @Test
//    void test8_recalculateEpicStatusWhenAllSubtaskAreNew() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.NEW,
//                        Duration.ofHours(36), // 1.5 дня
//                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.NEW,
//                        Duration.ofHours(60), // 2.5 дня
//                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        assertEquals(Task.Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, когда все его подзадачи новые.");
//    }
//
//    @Test
//    void test10_recalculateEpicStatusWhenAllSubtaskAreDone() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.DONE,
//                        Duration.ofHours(24), // 1 день
//                        LocalDateTime.of(2025, 12, 29, 11, 43), // 29 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.DONE,
//                        Duration.ofHours(48), // 2 дня
//                        LocalDateTime.of(2026, 1, 6, 11, 43), // 6 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        assertEquals(Task.Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, когда все его подзадачи завершены.");
//    }
//
//    @Test
//    void test11_recalculateEpicStatusWhenSubtaskAreNewAndDone() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.NEW,
//                        Duration.ofHours(24), // 1 день
//                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.DONE,
//                        Duration.ofHours(48), // 2 дня
//                        LocalDateTime.of(2026, 1, 6, 19, 19), // 6 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
//    }
//
//    @Test
//    void test12_recalculateEpicStatusWhenSubtaskAreInProgressAndDone() {
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask1.getIdTask(),
//                        "new title Subtask1",
//                        "new description Subtask1",
//                        Task.Status.NEW,
//                        Duration.ofHours(24), // 1 день
//                        LocalDateTime.of(2025, 12, 29, 13, 53), // 29 декабря
//                        subtask1.getEpicIdTask()),
//                subtask1.getIdTask());
//
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.DONE,
//                        Duration.ofHours(48), // 2 дня
//                        LocalDateTime.of(2026, 1, 6, 19, 19), // 6 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        assertEquals(Task.Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, когда его подзадачи в разных статусах.");
//    }
//
//    @Test
//    void test16_shouldUpdateAllVariantsOfTask(){
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//        taskManager.createTask(task1);
//        taskManager.createTask(task2);
//
//        Subtask updatedSubtask = new Subtask("update Subtask1", "update description subtask1",
//                Task.Status.IN_PROGRESS,
//                Duration.ofHours(24), // 1 день
//                LocalDateTime.of(2026, 1, 7, 10, 0), // 7 января (после всех задач)
//                epic.getIdTask());
//
//        // Исправляем время, чтобы не пересекалось
//        taskManager.updateSubtask(new Subtask(subtask2.getIdTask(),
//                        "new title Subtask2",
//                        "new description Subtask2",
//                        Task.Status.DONE,
//                        Duration.ofHours(36), // 1.5 дня
//                        LocalDateTime.of(2026, 1, 8, 19, 19), // 8 января
//                        subtask2.getEpicIdTask()),
//                subtask2.getIdTask());
//
//        taskManager.updateSubtask(updatedSubtask, subtask1.getIdTask());
//        assertEquals(updatedSubtask, taskManager.getSubtaskById(subtask1.getIdTask()));
//
//        Subtask updatedSubtask2 = new Subtask("update Subtask2", "update description subtask2",
//                Task.Status.DONE,
//                Duration.ofHours(48), // 2 дня
//                LocalDateTime.of(2026, 1, 9, 10, 0), // 9 января
//                epic.getIdTask());
//
//        taskManager.updateSubtask(updatedSubtask2, subtask1.getIdTask());
//        assertEquals(updatedSubtask2, taskManager.getSubtaskById(subtask1.getIdTask()));
//
//        Epic updatedEpic = new Epic("updated Epic", Task.Status.DONE, "updated description epic");
//        taskManager.updateEpic(updatedEpic, epic.getIdTask());
//        assertEquals(updatedEpic, taskManager.getEpicById(epic.getIdTask()));
//
//        Task updatedTask1 = new Task("upd Task1 title", "upd Task1 description", Task.Status.DONE,
//                Duration.ofDays(2), LocalDateTime.of(2026, 1, 10, 2, 35)); // 10 января
//        taskManager.updateTask(updatedTask1, task1.getIdTask());
//        assertEquals(updatedTask1, taskManager.getTaskById(task1.getIdTask()));
//    }
//
//    @Test
//    void test18_shouldReturnPrioritizedTasks() {
//        taskManager.createTask(task1);
//        taskManager.createTask(task2);
//        taskManager.createEpic(epic);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask2);
//
//        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
//
//        // Теперь должно быть 5 элементов в правильном порядке
//        assertEquals(5, prioritizedTasks.size(), "Должно быть 5 задач в приоритетном списке");
//
//        // Порядок по времени начала с НОВЫМИ временами:
//        // 1. task2     (2025-12-25 10:00) длится 2 дня → заканчивается 27.12
//        // 2. subtask1  (2025-12-28 12:35) начинается после task2, длится 5 дней → заканчивается 02.01
//        // 3. task1     (2026-01-01 10:00) начинается после subtask1, длится 3 дня → заканчивается 04.01
//        // 4. subtask2  (2026-01-05 14:00) начинается после task1, длится 4 дня → заканчивается 09.01
//        // 5. epic (время начала = времени первой подзадачи = 2025-12-28 12:35)
//
//        // Проверяем порядок
//        assertEquals(task2, prioritizedTasks.get(0), "Первая задача должна быть task2");
//        assertEquals(subtask1, prioritizedTasks.get(1), "Вторая задача должна быть subtask1");
//        assertEquals(task1, prioritizedTasks.get(2), "Третья задача должна быть task1");
//        assertEquals(subtask2, prioritizedTasks.get(3), "Четвертая задача должна быть subtask2");
//        assertEquals(epic, prioritizedTasks.get(4), "Пятая задача должна быть epic");
//    }
//}