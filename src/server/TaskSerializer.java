package server;

import model.Task;

import java.util.Collection;

public interface TaskSerializer {

  Task fromJson(String json);

  String toJson(Task task);

  String toJson(Collection<? extends Task> tasks);
}
