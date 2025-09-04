// src/ManagerTest.java
public class ManagerTest {
    public static void main(String[] args) {
        Manager manager = new Manager();

        // Тест создания задач
        Task task1 = manager.createTask("Task1", "Desc1");
        Task task2 = manager.createTask("Task2", "Desc2");
        Epic epic1 = manager.createEpic("Epic1", "EpicDesc1");
        Epic epic2 = manager.createEpic("Epic2", "EpicDesc2");
        Subtask sub1 = manager.createSubtask("Sub1", "SubDesc1", epic1);
        Subtask sub2 = manager.createSubtask("Sub2", "SubDesc2", epic1);

        // Тест получения задач
        System.out.println("getTask: " + manager.getTask(task1.getId()));
        System.out.println("getTaskById: " + manager.getTaskById(task2.getId()));

        // Тест получения списков
        System.out.println("getTaskList: " + manager.getTaskList());
        System.out.println("getEpicList: " + manager.getEpicList());
        System.out.println("getSubtaskList: " + manager.getSubtaskList());

        // Тест получения подзадач эпика
        System.out.println("getSubtasksOfEpic: " + manager.getSubtasksOfEpic(epic1));

        // Тест обновления задачи
        task1.updateTasksStatus(Task.Status.DONE);
        manager.updateTask(task1.getId(), task1);
        System.out.println("After updateTask: " + manager.getTask(task1.getId()));

        // Тест обновления статуса
        manager.updateStatus(sub1.getId(), Task.Status.DONE);
        System.out.println("After updateStatus: " + manager.getTask(sub1.getId()));

        // Тест удаления задачи по id
        manager.deleteTaskId(task2.getId());
        System.out.println("After deleteTaskId: " + manager.getTaskList());

        // Тест удаления всех задач
        manager.deleteAllTasks();
        System.out.println("After deleteAllTasks: " + manager.getTaskList());

        // Тест печати задач
        manager.createTask("Task3", "Desc3");
        manager.printTasks();
    }
}
