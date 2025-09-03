public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String task, String description, int epicId) {
        super(id, task, description);
        this.epicId = epicId;
    }
    public Subtask(int id, String task, String description, int epicId, Status status) {
        super(id, task, description, status);
        this.epicId = epicId;
    }

    public int getEpicId () {
        return epicId;
    }

    @Override
    public void updateTasksStatus(int id, Task task, Status status ) {

        this.status = status;
    }
}
