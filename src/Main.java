// TODO

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task1 = taskManager.createTask("Занести офисы в CRM", "Сделать это до конца дня");
        Task task2 = taskManager.createTask("Подготовить презентацию", "Презентация для встречи с клиентом");
        Task task3 = taskManager.createTask("Обновить сайт", "Добавить новую информацию на главную страницу");

        Epic epic1 = taskManager.createEpic("Запустить рекламную кампанию", "Кампания для нового продукта");
        Epic epic2 = taskManager.createEpic("Организовать мероприятие", "Мероприятие для клиентов");
        Epic epic3 = taskManager.createEpic("Разработать мобильное приложение", "Приложение для удобства клиентов");

        Subtask subtask1 = taskManager.createSubtask("Создать баннеры", "Баннеры для онлайн-рекламы", epic1);
        Subtask subtask2 = taskManager.createSubtask("Настроить таргетинг", "Таргетинг для соцсетей", epic1);
        Subtask subtask3 = taskManager.createSubtask("Забронировать место", "Место для проведения мероприятия", epic2);
        Subtask subtask4 = taskManager.createSubtask("Пригласить спикеров", "Спикеры для выступлений", epic2);
        Subtask subtask5 = taskManager.createSubtask("Разработать дизайн", "Дизайн для приложения", epic3);
        Subtask subtask6 = taskManager.createSubtask("Программирование", "Кодирование функционала", epic3);

        System.out.println("Список сабтасков: " + taskManager.getSubtaskList());
        System.out.println("Список тасков: " + taskManager.getTaskList());
        System.out.println("Список эпиков: " + taskManager.getEpicList());

        Task updTask1 = taskManager.createTask("Обновить сайт", "Добавить новую информацию на главную страницу", Task.Status.IN_PROGRESS);
        taskManager.updateTask(updTask1);
        System.out.println("Обновленный таск: " + taskManager.getTask(updTask1.getId()));
        Subtask updSubtask1 = taskManager.createSubtask("Создать баннеры", "Баннеры для онлайн-рекламы", epic1, Task.Status.IN_PROGRESS);
        taskManager.updateSubtask(updSubtask1);
        System.out.println("Обновленный сабтаск: " + taskManager.getTask(updSubtask1.getId())
        + "\n Эпик сабтаска: " + taskManager.getTask(((Subtask) taskManager.getTask(updSubtask1.getId())).getEpicId()));
        taskManager.deleteTaskId(updTask1.getId());
        System.out.println("Список тасков после удаления: " + taskManager.getTaskList());
    }
}
