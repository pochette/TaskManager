import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class EpicHandler extends TaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }
}
