import java.util.*;

public class Manager {
    private static int id = 0;//Для генерации идентификаторов можно использовать числовое поле класса менеджер, увеличиваия его на

    //нужно получить новое значение
    Map<Integer, Task> taskMap = new HashMap<>();

    public Task createTask (String title, String description) {
        Task task = new Task(id++, title, description, Task.Status.NEW);
        taskMap.put(task.getId(), task);
        return task;
    }

    public Epic createEpic (String title, String description) {
        Epic epic = new Epic(id++, title, description);
        taskMap.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask (String title, String description, Epic epic) {
        Subtask subtask = new Subtask(id++, title, description, epic.getId());
        taskMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        return subtask;
    }

//    public Subtask createSubtask (String title, String description, Epic epic, Task.Status status) {
//        Subtask subtask = new Subtask(id++, title, description, epic.getId(), status);
//        taskMap.put(subtask.getId(), subtask);
//        epic.addSubtask(subtask.getId());
//        return subtask;
//    }

    public Task getTask (int id) {
        return taskMap.get(id);
    }

    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Task) {
                taskList.add(value);
            }
        }
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Epic) {
                epicList.add((Epic) value);
            }
        }
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Task value : taskMap.values()) {
            if (value instanceof Subtask) {
                subtaskList.add( (Subtask) value);
            }
        }
        return subtaskList;
    }

    public void deleteAllTasks () {
        taskMap.clear();
    }

    public void updateTask (int id, Task task) {
        taskMap.replace(id, task);
    }

//    Добавить потом в каждое обновление статусов сабтасков
    public void updateEpicStatus(Epic epic) {
        if (epic.getSubtaskSet().isEmpty()) {
            epic.updateTasksStatus(Task.Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (Integer subId : epic.getSubtaskSet()) {
            Subtask subtask = (Subtask) taskMap.get(subId);
            if (subtask != null) {
                Task.Status st = subtask.getStatus();
                if (st != Task.Status.NEW) {
                    allNew = false;
                }
                if (st != Task.Status.DONE) {
                    allDone = false;
                }
            }
        }
        if (allNew) {
            epic.updateTasksStatus(Task.Status.NEW);
        } else if (allDone) {
            epic.updateTasksStatus(Task.Status.DONE);
        } else {
            epic.updateTasksStatus(Task.Status.IN_PROGRESS);
        }
    }

    public void updateStatus (int id, Task.Status status) {
        Task tempTask = taskMap.get(id);
        if (tempTask == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
        } else if ( !(tempTask instanceof Epic)) {
            tempTask.updateTasksStatus(status);
            taskMap.replace(id, tempTask);
        } else {
            updateEpicStatus((Epic)tempTask);
        }
    }

    public void deleteTaskId (int id) {
        taskMap.remove(id);
    }
    public List<Subtask> getSubtasksOfEpic (Epic epic) {
        Set<Integer> subtaskSet = new HashSet<>(epic.getSubtaskSet());
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer subId : subtaskSet) {
            subtaskList.add((Subtask) taskMap.get(subId));
        }
        return subtaskList;
    }
}
//Ваш проект реализует менеджер задач с поддержкой задач, эпиков и подзадач. Вот основные замечания и рекомендации:
//
//        **1. Ошибка в методе getId класса Task**
//Метод вызывает сам себя, что приведёт к StackOverflowError.
//```java
//public int getId () {
//    return this.getId(); // ошибка
//}
//```
//        **Исправить на:**
//        ```java
//public int getId () {
//    return this.id;
//}
//```
//
//        **2. Нарушение инкапсуляции**
//Поля `title`, `description`, `status` в `Task` и `subtaskSet` в `Epic` лучше сделать private и добавить геттеры/сеттеры.
//
//**3. Дублирование кода**
//В `Manager` методы `getTaskList`, `getEpicList`, `getSubtaskList` проходят по всей карте задач. Можно оптимизировать, если задачи будут храниться в отдельных коллекциях.
//
//        **4. Неиспользуемый Map в Main**
//В классе `Main` объявлен `taskMap`, который нигде не используется. Лучше убрать.
//
//        **5. Конструктор Task**
//В конструкторе `Task` не инициализируется поле `id`.
//
//        **6. Метод updateTasksStatus в Subtask**
//Переопределён, но не используется и не нужен, если не добавляется новая логика.
//
//        **7. Метод updateEpicStatus**
//Вызов `epic.updateTasksStatus` подразумевает, что статус эпика меняется только через этот метод. Это хорошо, но стоит явно указать, что статус эпика нельзя менять напрямую.
//
//        **8. Стиль кода**
//        - Следует придерживаться единого стиля именования (например, task vs title).
//        - Комментарии лучше писать на английском или придерживаться одного языка.
//
//**9. Потенциальная проблема с id**
//Статическое поле `id` в `Manager` не потокобезопасно. Если планируется многопоточность — использовать AtomicInteger.
//
//        **10. Нет equals/hashCode**
//Для корректной работы коллекций стоит переопределить методы `equals` и `hashCode` в сущностях.
//
//        **11. Нет проверки на существование эпика при создании подзадачи**
//В методах создания подзадачи не проверяется, что переданный epic существует в `taskMap`.
//
//        ---
//
//        **Резюме:**
//Проект реализован в целом верно, но требует исправления ошибок с геттерами, улучшения инкапсуляции, оптимизации хранения задач и доработки некоторых методов.