public class Subtask extends Task {
    private final int epicIdTask;

    public Subtask(int idTask, String title, String description, Status status, int epicIdTask) {
        super(title, description);
        this.epicIdTask = epicIdTask;
    }

    public Subtask(String title, String description, Status status, int epicIdTask) {
        super(title, description, status);
        this.epicIdTask = epicIdTask;
    }


    @Override
    public TypesOfTask getType() {
        return TypesOfTask.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicIdTask=" + epicIdTask +
                ", id=" + this.getIdTask() +
                ", title='" + this.getTitle() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() +
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
