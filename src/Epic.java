public class Epic extends Task{
    protected int id;
    protected String task;
    protected String description;
    protected enum status {
        NEW,
        DONE,
        IN_PROGRESS
    };
}
