import java.util.HashMap;
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = manager.createTask("Занести офисы в CRM", "Сделать это до конца дня");
        Task task2 = manager.createTask("Подготовить презентацию", "Презентация для встречи с клиентом");
        Task task3 = manager.createTask("Обновить сайт", "Добавить новую информацию на главную страницу");

        Epic epic1 = manager.createEpic("Запустить рекламную кампанию", "Кампания для нового продукта");
        Epic epic2 = manager.createEpic("Организовать мероприятие", "Мероприятие для клиентов");
        Epic epic3 = manager.createEpic("Разработать мобильное приложение", "Приложение для удобства клиентов");

        Subtask subtask1 = manager.createSubtask("Создать баннеры", "Баннеры для онлайн-рекламы", epic1);
        Subtask subtask2 = manager.createSubtask("Настроить таргетинг", "Таргетинг для соцсетей", epic1);
        Subtask subtask3 = manager.createSubtask("Забронировать место", "Место для проведения мероприятия", epic2);
        Subtask subtask4 = manager.createSubtask("Пригласить спикеров", "Спикеры для выступлений", epic2);
        Subtask subtask5 = manager.createSubtask("Разработать дизайн", "Дизайн для приложения", epic3);
        Subtask subtask6 = manager.createSubtask("Программирование", "Кодирование функционала", epic3);

        // Вывод всех задач
        System.out.println("Все задачи: ");

//
//        Task task1 = manager.createTask("Task 1", "Description for Task 1", Task.Status.NEW);
//        Task task2 = manager.createTask("Task 2", "Description for Task 2", Task.Status.IN_PROGRESS);
//
//        Epic epic1 = manager.createEpic("Epic 1", "Description for Epic 1");
//        Epic epic2 = manager.createEpic("Epic 2", "Description for Epic 2");
//
//        Subtask subtask1 = manager.createSubtask("Subtask 1", "Description for Subtask 1", epic1, Task.Status.NEW);
//        Subtask subtask2 = manager.createSubtask("Subtask 2", "Description for Subtask 2", epic1, Task.Status.DONE);
//        Subtask subtask3 = manager.createSubtask("Subtask 3", "Description for Subtask 3", epic2);
//
//        System.out.println("All Tasks:");
//        for (Task task : manager.getTaskList()) {
//            System.out.println("ID: " + task.getId() + ", Title: " + task.title + ", Status: " + task.getStatus());
//        }
//
//        System.out.println("\nAll Epics:");
//        for (Epic epic : manager.getEpicList()) {
//            System.out.println("ID: " + epic.getId() + ", Title: " + epic.title + ", Status: " + epic.getStatus() +
//                    ", Subtasks: " + epic.getSubtaskSet());
//        }
//
//        System.out.println("\nAll Subtasks:");
//        for (Subtask subtask : manager.getSubtaskList()) {
//            System.out.println("ID: " + subtask.getId() + ", Title: " + subtask.title + ", Status: " + subtask.getStatus() +
//                    ", Epic ID: " + subtask.getEpicId());
//        }
//
//
//    }
    }
}