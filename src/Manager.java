public abstract class Manager implements TaskManager {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

}
