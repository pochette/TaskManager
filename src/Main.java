import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        // 1. Инициализация менеджеров
        TaskManager taskManager = Manager.getDefault();
        HistoryManager historyManager = Manager.getDefaultHistory(); // Получаем тот же инстанс, что и в taskManager

        // 2. Создание задач
        System.out.println("\n--- Создаём задачи ---");
        Task task1 = new Task("Простая задача 1", "Описание для задачи 1", Task.Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("Простая задача 2", "Описание для задачи 2", Task.Status.IN_PROGRESS);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Большой эпик 1", "Описание для эпика 1");
        taskManager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask("Подзадача 1.1", "Для эпика 1", epic1.getId(), Task.Status.NEW);
        taskManager.createSubtask(subtask1_1);
        Subtask subtask1_2 = new Subtask("Подзадача 1.2", "Для эпика 1", epic1.getId(), Task.Status.NEW);
        taskManager.createSubtask(subtask1_2);

        Epic epic2 = new Epic("Большой эпик 2", "Описание для эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask2_1 = new Subtask("Подзадача 2.1", "Для эпика 2", epic2.getId(), Task.Status.DONE);
        taskManager.createSubtask(subtask2_1);

        printAllTasks(taskManager);

        // 3. Тестирование получения и истории
        System.out.println("\n--- Получаем задачи и смотрим историю ---");
        taskManager.getTask(task1.getId());
        taskManager.getTask(epic1.getId());
        taskManager.getTask(subtask1_1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(epic2.getId());
        taskManager.getTask(subtask2_1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(epic2.getId());
        taskManager.getTask(subtask2_1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(epic2.getId());
        taskManager.getTask(subtask2_1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());// Повторный вызов для проверки уникальности в истории

        List<Task> history = historyManager.getHistory();
        System.out.println("История просмотров (" + history.size() + " записей):");
        for (Task t : history.reversed()) {
            System.out.println(t);
        }


        // 4. Тестирование обновления
        System.out.println("\n--- Обновляем задачи и проверяем статусы ---");

        // Обновляем статус подзадачи и проверяем, изменился ли статус эпика
        System.out.println("Статус Эпика 1 до обновления подзадачи: " + epic1.getStatus());
        Subtask updatedSubtask = new Subtask("Обновленная Подзадача 1.2", "Новое описание", epic1.getId(), Task.Status.DONE);
        taskManager.updateSubtask(updatedSubtask, subtask1_1.getId()); // Устанавливаем старый ID
        System.out.println("Статус Эпика 1 после обновления подзадачи: " + epic1.getStatus()); // Должен быть IN_PROGRESS


        // Обновляем вторую подзадачу
        Subtask updatedSubtask2 = new Subtask("Обновленная Подзадача 1.1", "Новое описание", epic1.getId(), Task.Status.DONE);
        taskManager.updateSubtask(updatedSubtask2, subtask1_2.getId());
        System.out.println("Статус Эпика 1 после обновления второй подзадачи: " + epic1.getStatus()); // Должен быть DONE

        printAllTasks(taskManager);

        // 5. Тестирование удаления
        System.out.println("\n--- Удаляем задачи ---");

        // Удаляем простую задачу
        System.out.println("Удаляем Задачу 2 (ID=" + task2.getId() + ")");
        taskManager.deleteTaskId(task2.getId());
        System.out.println("Задача 2 после удаления: " + taskManager.getTask(task2.getId())); // Должно быть null

        // Удаляем эпик и проверяем, что его подзадачи тоже удалены
        System.out.println("Удаляем Эпик 1 (ID=" + epic1.getId() + ")");
        taskManager.deleteTaskId(epic1.getId());
        System.out.println("Эпик 1 после удаления: " + taskManager.getTask(epic1.getId())); // Должно быть null
        System.out.println("Подзадача 1.1 после удаления эпика: " + taskManager.getTask(subtask1_1.getId())); // Должно быть null

        System.out.println("\nИтоговый список задач:");
        printAllTasks(taskManager);

        // Проверяем, что удаленные задачи исчезли из истории
        history = historyManager.getHistory();
        System.out.println("\nИтоговая история просмотров (" + history.size() + " записей):");
        for (Task t : history) {
            System.out.println(t);
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n== Текущий список всех задач ==");
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println("  -> " + task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println("  -> " + epic);
            for (Subtask subtask : manager.getSubtasksOfEpic(epic)) {
                System.out.println("    -- " + subtask);
            }
        }
        System.out.println("================================");
    }
}