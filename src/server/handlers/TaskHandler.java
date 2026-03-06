package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.NotFoundException;
import service.TaskTimeOverlapException;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.HttpCodeResponse.SERVER_ERROR;
import static server.HttpRequestMethods.*;

public class TaskHandler extends BaseHTTPHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var httpRequestMethod = exchange.getRequestMethod();

        try {
            switch (httpRequestMethod) {
                case GET -> getByRequest(exchange);
                case POST -> postByRequest(exchange);
                case DELETE -> deleteByRequest(exchange);
                default -> sendResponse(exchange, "405, Method not allowed");
            }
        } catch (TaskTimeOverlapException e) {
            System.out.println(e.getMessage());
            sendHasOverLap(exchange);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            exchange.sendResponseHeaders(SERVER_ERROR.getCode(), 0);
        } finally {
            exchange.close();
        }
    }

    protected void getByRequest(HttpExchange exchange) throws IOException, NotFoundException {
        final var path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks$", path)) {
            String response = taskManager.getListOfAllTasks().stream()
                .flatMap(Stream::ofNullable)
                .map(taskSerializer::toJson)
                .collect(Collectors.joining(",", "[", "]"));
            sendResponse(exchange, response);
            return;
        }
        if (Pattern.matches("^/tasks/\\d+$", path)) {
            var id = parsTaskId(path.replaceFirst("/tasks/", ""));
            if (id != -1) {
                var task = taskManager.getTaskById(id);
                sendResponse(exchange, taskSerializer.toJson(task));
            }
        } else {
            sendNotFound(exchange, "Задача с таким id не найдена");
        }
    }

    protected void postByRequest(HttpExchange exchange) throws IOException, TaskTimeOverlapException {
        var path = exchange.getRequestURI().getPath();
        var body = getBody(exchange);

    }

    protected void deleteByRequest(HttpExchange exchange) throws IOException {

    }
}

