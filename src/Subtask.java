public class Subtask extends Task {
    private final int epicIdTask;

    public Subtask(String title, String description, int epicIdTask) {
        super(title, description);
        this.epicIdTask = epicIdTask;
    }
    @Override
    public TypesOfTask getType() {
        return TypesOfTask.SUBTASK;
    }

    public Subtask(String task, String description, int epicIdTask, Status status) {
        super(task, description, status);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(Integer id, String title, Status status, String description, int epicIdTask) {
        super(id, title, status, description);
        this.epicIdTask = epicIdTask;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicIdTask=" + epicIdTask +
                ", id=" + this.getIdTask() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toString(Subtask subtask) {
        return subtask.getIdTask() + "," +
                subtask.getType() + "," +
                subtask.getTitle() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicIdTask();
    }


    public int getEpicIdTask() {
        return epicIdTask;
    }
}
