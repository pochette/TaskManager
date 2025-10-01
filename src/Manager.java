public final class Manager {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();
    private static final TaskManager taskManager = new InMemoryTaskManager(historyManager);

    private Manager() {}


    public static TaskManager getDefault() {
        return taskManager;
    }
    public static HistoryManager getDefaultHistory() {
       return historyManager;
    }

}
