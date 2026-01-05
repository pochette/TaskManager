import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicIdTask;

    // конструктор для восстановления из файла c id
    public Subtask(int idTask, String title, String description, Status status,
                   Duration duration, LocalDateTime startTime,
                   int epicIdTask) {
        super(idTask, title, description, status, duration, startTime);
        this.epicIdTask = epicIdTask;
    }

    // конструктор для создания нового подзадачи без id
    public Subtask(String title, String description,
                   Status status, Duration duration,
                   LocalDateTime startTime, int epicIdTask) {
        super(title, description, status, duration, startTime);
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

//    @Override
//    public String toString() {
//        return "Subtask{" +
//                "epicIdTask=" + this.getEpicIdTask() +
//                ", id=" + this.getIdTask() +
//                ", title='" + this.getTitle() + '\'' +
//                ", description='" + this.getDescription() + '\'' +
//                ", status=" + this.getStatus() +
//                '}';
//    }
}
