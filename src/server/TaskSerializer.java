package server;

import com.google.gson.Gson;
import model.Task;

public interface TaskSerializer {

  Task fromJson(String json);

  String toJson(Task task);
}
