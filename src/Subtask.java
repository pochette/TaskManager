public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }
    @Override
    public TypesOfTask getType() {
        return TypesOfTask.SUBTASK;
    }

    public Subtask(String task, String description, int epicId, Status status) {
        super(task, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, Status status, String description, int epicId) {
        super(id, title, status, description);
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," +
                subtask.getType() + "," +
                subtask.getTitle() + "," +
                subtask.getStatus() + "," +
                subtask.getDescription() + "," +
                subtask.getEpicId();
    }


    public int getEpicId() {
        return epicId;
    }
}
