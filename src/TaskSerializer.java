public interface TaskSerializer {
        Task fromJson(String json);

    String toJson(Task task);
}

