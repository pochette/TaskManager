import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicIdTask;

    public Subtask(int idTask, String title, String description, Status status, int epicIdTask) {
        super(idTask, title, description, status, TypesOfTask.SUBTASK);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(String title, String description, Status status, int epicIdTask) {
        super(title, description, status, TypesOfTask.SUBTASK);
        this.epicIdTask = epicIdTask;
    }

    // конструктор для восстановления из файла
    public Subtask(int idTask, String title, String description, Status status, Duration duration, LocalDateTime startTime,
                   TypesOfTask typesOfTask, int epicIdTask) {
        super(idTask, title, description, status, duration, startTime, typesOfTask);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(String title, String description, TypesOfTask typesOfTask, Status status, Duration duration,
                   LocalDateTime startTime, int epicIdTask) {
        super(title, description, typesOfTask, status, duration, startTime);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(String title, String description, Status status, TypesOfTask typesOfTask, int epicIdTask) {
        super(title, description, status, typesOfTask);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(String title, String description, int epicIdTask) {
        super(title, description);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(int idTask, String title, String description, Status status, TypesOfTask typesOfTask, int epicIdTask) {
        super(idTask, title, description, status, typesOfTask);
        this.epicIdTask = epicIdTask;
    }


    public int getEpicIdTask() {
        return epicIdTask;
    }

    @Override
    public TypesOfTask getType() {
        return TypesOfTask.SUBTASK;
    }


    public String toString(Subtask subtask) {
        return subtask.getIdTask() + "," +
                subtask.getType() + "," +
                subtask.getTitle() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getDuration() + "," +
                subtask.getStartTime() + "," +
                subtask.getEpicIdTask();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicIdTask=" + this.getEpicIdTask() +
                ", id=" + this.getIdTask() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
