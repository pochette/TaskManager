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

        System.out.println("Список сабтасков: " + manager.getSubtaskList());
        System.out.println("Список тасков: " + manager.getTaskList());
        System.out.println("Список эпиков: " + manager.getEpicList());

        Task updTask1 = manager.createTask("Обновить сайт", "Добавить новую информацию на главную страницу", Task.Status.IN_PROGRESS);
        manager.updateTask(updTask1);
        System.out.println("Обновленный таск: " + manager.getTask(updTask1.getId()));
        Subtask updSubtask1 = manager.createSubtask("Создать баннеры", "Баннеры для онлайн-рекламы", epic1, Task.Status.IN_PROGRESS);
        manager.updateSubtask(updSubtask1);
        System.out.println("Обновленный сабтаск: " + manager.getTask(updSubtask1.getId())
        + "\n Эпик сабтаска: " + manager.getTask(((Subtask) manager.getTask(updSubtask1.getId())).getEpicId()));
        manager.deleteTaskId(updTask1.getId());
        System.out.println("Список тасков после удаления: " + manager.getTaskList());
    }
}
