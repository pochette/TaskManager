public class Subtask extends Task {
    private final int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }
    public Subtask(String task, String description, int epicId, Status status) {
        super(task, description, status);
        this.epicId = epicId;
    }
//    public Subtask (int id, String title, String description, Status status, int epicId) {
//        super(id, title, description, status);
//        this.epicId = epicId;
//    }


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

    public int getEpicId () {
        return epicId;
    }
}
