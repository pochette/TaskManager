import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

public abstract class TaskHandler implements HttpHandler {
  protected final TaskManager taskManager = Managers.getDefault();

  @Override
  public void handle(HttpExchange exchange) throws IOException {}
}
