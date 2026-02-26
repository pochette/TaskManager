import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    private static final TaskSerializer serializer = null;

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);

    }




}

class GetEpicHandler extends TaskHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (var os = exchange.getResponseBody()) {
            this.taskManager.getEpicList();

        }

    }
}
