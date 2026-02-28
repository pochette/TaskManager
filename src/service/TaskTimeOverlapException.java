package service;

public class TaskTimeOverlapException extends RuntimeException {
    public TaskTimeOverlapException(String message) {
        super(message);
    }
}
