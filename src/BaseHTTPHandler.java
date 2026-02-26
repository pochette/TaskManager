import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Duration;




public abstract class BaseHTTPHandler implements HttpHandler {
    protected TaskManager taskManager = Managers.getDefault();
    protected TaskSerializer taskSerializer = null;

    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(DurationAdapter.class, new DurationAdapter());
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    protected void sendResponse(HttpExchange httpExchange, String text ) throws IOException {

    }

    protected void send

}
