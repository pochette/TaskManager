import com.google.gson.Gson;
import com.google.gson.GsonBuilder;



public interface TaskSerializer {
    String toJson(Task task);
    Task fromJson(String json);

}
