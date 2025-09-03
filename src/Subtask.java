public class Subtask extends Task{
    private final int epicId;

    public Subtask(int id, String task, String description, int epicId) {
        super(id, task, description);
        this.epicId = epicId;

    }
    public int getEpicId () {
        return epicId;
    }
}
