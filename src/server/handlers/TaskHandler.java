package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.NotFoundException;
import service.TaskTimeOverlapException;

import java.io.IOException;
import java.net.http.HttpRequest;

public class TaskHandler extends BaseHTTPHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpRequest httpRequest = exchange.
    }

    protected void getByRequest(HttpExchange exchange) throws IOException, NotFoundException {

    }

    protected void postByRequest(HttpExchange exchange) throws IOException,TaskTimeOverlapException {

    }

    protected void deleteByRequest(HttpExchange exchange) throws IOException {

    }
}
