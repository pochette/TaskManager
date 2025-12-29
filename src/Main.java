public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();
        HistoryManager historyManager = Manager.getDefaultHistory();

        // 1. Создание задач
        Task task1 = new Task( "Задача 1", "Описание задачи 1", Task.Status.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", Task.Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с подзадачами");
        taskManager.createEpic(epic1);

        Subtask sub1 = new Subtask("Подзадача 1", "К Эпику 1", Task.Status.NEW, epic1.getIdTask());
        Subtask sub2 = new Subtask("Подзадача 2", "К Эпику 1", Task.Status.NEW, epic1.getIdTask());
        Subtask sub3 = new Subtask("Подзадача 3", "К Эпику 1", Task.Status.NEW, epic1.getIdTask());
        taskManager.createSubtask(sub1);
        taskManager.createSubtask(sub2);
        taskManager.createSubtask(sub3);

        Epic epic2 = new Epic("Эпик 2", "Эпик без подзадач");
        taskManager.createEpic(epic2);

        // 2. Запрос задач в разном порядке
        System.out.println("=== Запрос задач ===");
        taskManager.getTaskById(task1.getIdTask());
        printHistory(historyManager);

        taskManager.getTaskById(epic1.getIdTask());
        printHistory(historyManager);

        taskManager.getTaskById(sub2.getIdTask());
        printHistory(historyManager);

        taskManager.getTaskById(task2.getIdTask());
        printHistory(historyManager);

        taskManager.getTaskById(epic2.getIdTask());
        printHistory(historyManager);

        taskManager.getTaskById(task1.getIdTask()); // повторный запрос
        printHistory(historyManager);

        // 3. Удаляем задачу, которая есть в истории
        System.out.println("\n=== Удаляем задачу 1 ===");
        taskManager.removeTaskById(task1.getIdTask());
        printHistory(historyManager);

        // 4. Удаляем эпик с тремя подзадачами
        System.out.println("\n=== Удаляем эпик 1 вместе с его подзадачами ===");
        taskManager.removeTaskById(epic1.getIdTask());
        printHistory(historyManager);
    }

    private static void printHistory(HistoryManager historyManager) {
        System.out.println("История: ");
        for (Task t : historyManager.getHistory()) {
            System.out.println(" - " + t);
        }
        System.out.println();
    }
}
