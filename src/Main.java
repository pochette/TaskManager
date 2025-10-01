import java.util.List;



//todo Исправить класс Main и дописать тест.
//TODO после добавления задач в historyManager  она пустая. Исправить методы в классе InMemoryHistoryManager


public class Main {
    public static void main(String[] args) {
        System.out.println("=== ТЕСТИРОВАНИЕ ПО ТЕХНИЧЕСКОМУ ЗАДАНИЮ ===\n");

        // 1. Инициализация менеджеров
        TaskManager taskManager = Manager.getDefault();
        HistoryManager historyManager = Manager.getDefaultHistory();

        // 2. Создание задач по ТЗ
        System.out.println("1. СОЗДАНИЕ ЗАДАЧ:");
        System.out.println("-------------------");

        // Две простые задачи
        Task task1 = new Task("Простая задача 1", "Описание 1", Task.Status.NEW);
        Task task2 = new Task("Простая задача 2", "Описание 2", Task.Status.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        System.out.println("Созданы простые задачи: ID=" + task1.getId() + ", " + task2.getId());

        // Эпик с тремя подзадачами
        Epic epicWithSubtasks = new Epic("Эпик с подзадачами", "Описание эпика 1");
        taskManager.createEpic(epicWithSubtasks);
        System.out.println("Создан эпик с подзадачами: ID=" + epicWithSubtasks.getId());

        Subtask subtask1 = new Subtask("Подзадача 1", "Для эпика 1", epicWithSubtasks.getId(), Task.Status.NEW);
        Subtask subtask2 = new Subtask("Подзадача 2", "Для эпика 1", epicWithSubtasks.getId(), Task.Status.IN_PROGRESS);
        Subtask subtask3 = new Subtask("Подзадача 3", "Для эпика 1", epicWithSubtasks.getId(), Task.Status.DONE);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        System.out.println("Созданы подзадачи: ID=" + subtask1.getId() + ", " + subtask2.getId() + ", " + subtask3.getId());

        // Эпик без подзадач
        Epic epicWithoutSubtasks = new Epic("Эпик без подзадач", "Описание эпика 2");
        taskManager.createEpic(epicWithoutSubtasks);
        System.out.println("Создан эпик без подзадач: ID=" + epicWithoutSubtasks.getId());

        printAllTasks(taskManager);

        // 3. Тестирование истории (по ТЗ: "запросите созданные задачи несколько раз в разном порядке")
        System.out.println("\n2. ТЕСТИРОВАНИЕ ИСТОРИИ:");
        System.out.println("------------------------");

        System.out.println("Запрашиваем задачи в разном порядке:");

        // Первый круг запросов
        taskManager.getTask(task2.getId());      // простая задача 2
        taskManager.getTask(epicWithSubtasks.getId()); // эпик с подзадачами
        taskManager.getTask(subtask1.getId());   // подзадача 1
        taskManager.getTask(task1.getId());      // простая задача 1
        taskManager.getTask(epicWithoutSubtasks.getId()); // эпик без подзадач

        printHistory("История после первого круга запросов:", historyManager);

        // Второй круг запросов (добавляем дубликаты)
        System.out.println("\nДобавляем дубликаты и новые задачи:");
        taskManager.getTask(task1.getId());      // дубликат (должен переместиться в конец)
        taskManager.getTask(subtask2.getId());   // новая подзадача
        taskManager.getTask(epicWithSubtasks.getId()); // дубликат эпика
        taskManager.getTask(subtask3.getId());   // новая подзадача
        taskManager.getTask(task2.getId());      // дубликат

        printHistory("История после добавления дубликатов:", historyManager);

        // Проверяем, что в истории нет повторов
        System.out.println("\nПроверка на отсутствие дубликатов:");
        checkForDuplicates(historyManager.getHistory());

        // 4. Тестирование удаления (по ТЗ: "удалите задачу, которая есть в истории")
        System.out.println("\n3. ТЕСТИРОВАНИЕ УДАЛЕНИЯ:");
        System.out.println("------------------------");

        System.out.println("Удаляем задачу ID=" + task1.getId() + " (есть в истории)");
        taskManager.deleteTaskId(task1.getId());

        printHistory("История после удаления задачи " + task1.getId() + ":", historyManager);

        // 5. Тестирование удаления эпика с подзадачами (по ТЗ)
        System.out.println("\n4. ТЕСТИРОВАНИЕ УДАЛЕНИЯ ЭПИКА С ПОДЗАДАЧАМИ:");
        System.out.println("---------------------------------------------");

        System.out.println("Удаляем эпик с подзадачами ID=" + epicWithSubtasks.getId());
        taskManager.deleteTaskId(epicWithSubtasks.getId());

        printHistory("История после удаления эпика " + epicWithSubtasks.getId() + ":", historyManager);

        // Проверяем, что подзадачи удалены из истории
        System.out.println("\nПроверяем удаление подзадач из истории:");
        checkSubtasksRemoved(historyManager.getHistory(), subtask1.getId(), subtask2.getId(), subtask3.getId());

        // Финальная проверка
        System.out.println("\n5. ФИНАЛЬНАЯ ПРОВЕРКА:");
        System.out.println("----------------------");
        printAllTasks(taskManager);
        printHistory("Финальная история просмотров:", historyManager);

        System.out.println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===");
    }

    private static void printHistory(String message, HistoryManager historyManager) {
        System.out.println(message);
        List<Task> history = historyManager.getHistory();
        if (history.isEmpty()) {
            System.out.println("  История пуста");
        } else {
            for (int i = 0; i < history.size(); i++) {
                Task task = history.get(i);
                System.out.println("  " + (i + 1) + ". ID=" + task.getId() +
                        ", Название: " + task.getTitle() +
                        ", Тип: " + task.getClass().getSimpleName());
            }
        }
        System.out.println("  Всего записей: " + history.size());
    }

    private static void checkForDuplicates(List<Task> history) {
        java.util.Set<Integer> uniqueIds = new java.util.HashSet<>();
        boolean hasDuplicates = false;

        for (Task task : history) {
            if (!uniqueIds.add(task.getId())) {
                System.out.println("  ОШИБКА: Найден дубликат задачи ID=" + task.getId());
                hasDuplicates = true;
            }
        }

        if (!hasDuplicates) {
            System.out.println("  ✓ Дубликатов не найдено - проверка пройдена");
        }
    }

    private static void checkSubtasksRemoved(List<Task> history, int... subtaskIds) {
        boolean allRemoved = true;

        for (int subtaskId : subtaskIds) {
            boolean found = false;
            for (Task task : history) {
                if (task.getId() == subtaskId) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("  ОШИБКА: Подзадача ID=" + subtaskId + " осталась в истории");
                allRemoved = false;
            }
        }

        if (allRemoved) {
            System.out.println("  ✓ Все подзадачи удалены из истории - проверка пройдена");
        }
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nТекущее состояние менеджера задач:");
        System.out.println("Задачи: " + manager.getTaskList().size());
        for (Task task : manager.getTaskList()) {
            System.out.println("  [Task] ID=" + task.getId() + ": " + task.getTitle() + " (" + task.getStatus() + ")");
        }

        System.out.println("Эпики: " + manager.getEpicList().size());
        for (Epic epic : manager.getEpicList()) {
            System.out.println("  [Epic] ID=" + epic.getId() + ": " + epic.getTitle() + " (" + epic.getStatus() + ")");
            List<Subtask> subtasks = manager.getSubtasksOfEpic(epic);
            for (Subtask subtask : subtasks) {
                System.out.println("    [Subtask] ID=" + subtask.getId() + ": " + subtask.getTitle() + " (" + subtask.getStatus() + ")");
            }
        }
    }
}