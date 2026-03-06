package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public abstract class BaseHTTPHandler implements HttpHandler {
    protected TaskManager taskManager = Managers.getDefault();
    protected TaskSerializer taskSerializer = new JsonTaskSerializer();


    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        exchange.sendResponseHeaders(HttpCodeResponse.NOT_FOUND.getCode(), 0);
        exchange.getResponseBody().write(message.getBytes(StandardCharsets.UTF_8));
    }

    protected void sendHasOverLap(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpCodeResponse.OVERLAP.getCode(), 0);
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpCodeResponse.NOT_ALLOWED.getCode(), 0);
    }

    protected Integer parsTaskId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException numberFormatException) {
            return -1;
        }
    }

    protected void sendResponse(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(HttpCodeResponse.OK.getCode(), response.length);
        httpExchange.getResponseBody().write(response);
    }
    protected String getBody(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }



}
