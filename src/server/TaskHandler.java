package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.NotFoundException;
import service.TaskTimeOverlapException;

import java.io.IOException;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHTTPHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpRequestMethods requestMethods = HttpRequestMethods.valueOf(exchange.getRequestMethod());
        try {
            switch (requestMethods) {
                case GET :
                    getByRequest(exchange);
                    break;
                case POST:
                    postByRequest(exchange);
                    break;
                case DELETE:
                    deleteByRequest(exchange);
                    break;
            }
        } catch (NotFoundException notFoundException)  {
            System.out.println(notFoundException.getMessage());
            sendNotFound(exchange, notFoundException.getMessage());
        } catch (TaskTimeOverlapException taskTimeOverlapException) {
            System.out.println(taskTimeOverlapException.getMessage());
            sendHasOverLap(exchange);
        } catch (Exception e) {
            exchange.sendResponseHeaders(HttpCodeResponse.SERVER_ERROR.getCode(), 0);
        } finally {
            exchange.close();
        }

    }

    protected void getByRequest(HttpExchange exchange) throws IOException, NotFoundException {
        String path = exchange.getRequestURI().getPath();
        if(Pattern.matches("^/tasks$", path)) {
            String response = getGson().toJson(taskManager.getListOfAllTasks());
            sendResponse(exchange, response);
            return;
        } if(Pattern.matches("^/tasks/\\d+$", path)) {
            Integer id = parsTaskId(path.replaceFirst("/tasks/", ""));
            String response = getGson().toJson(taskManager.getTaskById(id));
            sendResponse(exchange, response);
        }
    }

    protected void postByRequest(HttpExchange exchange) throws IOException,TaskTimeOverlapException {

    }

    protected void deleteByRequest(HttpExchange exchange) throws IOException {

    }
}
