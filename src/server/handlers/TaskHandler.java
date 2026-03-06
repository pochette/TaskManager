package server.handlers;

import static server.HttpCodeResponse.BAD_REQUEST;
import static server.HttpCodeResponse.SERVER_ERROR;
import static server.HttpRequestMethods.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import model.Epic;
import model.Subtask;
import model.TypesOfTask;
import server.HttpCodeResponse;
import service.NotFoundException;
import service.TaskTimeOverlapException;

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
      // TODO дописать Общий catch (Exception e) слишком широкий. Он ловит все, включая
      // RuntimeException, что может скрывать баги. Лучше ловить конкретные исключения
      // (например, IOException, IllegalArgumentException). Для неизвестных ошибок
      // отправляйте 500, но логируйте детали.
      System.out.println(e.getMessage());
      exchange.sendResponseHeaders(SERVER_ERROR.getCode(), 0);
    } finally {
      exchange.close();
    }
  }

  protected void getByRequest(HttpExchange exchange) throws IOException, NotFoundException {
    final var path = exchange.getRequestURI().getPath();
    if (Pattern.matches("^/tasks$", path)) {
      String response =
          taskManager.getListOfAllTasks().stream()
              .flatMap(Stream::ofNullable)
              .map(taskSerializer::toJson)
              .collect(Collectors.joining(",", "[", "]"));
      sendResponse(exchange, response);
      return;
    }
    if (Pattern.matches("^/tasks/task/\\d+$", path)) {
      var id = parseTaskId(path.replaceFirst("/tasks/", ""));
      if (id > 0) {
        var task = taskManager.getTaskById(id);
        sendResponse(exchange, taskSerializer.toJson(task));
      }
    } else {
      exchange.sendResponseHeaders(BAD_REQUEST.getCode(), 0);
      sendNotFound(exchange, "Задача с таким id не найдена");
    }
  }

  protected void postByRequest(HttpExchange exchange) throws IOException, TaskTimeOverlapException {
    var path = exchange.getRequestURI().getPath();
    var body = getBody(exchange);
    if (Pattern.matches("^/tasks$", path)) {
      var task = taskSerializer.fromJson(body);
      try {
        TypesOfTask type = task.getType();
        if (taskManager.getTaskById(task.getIdTask()) != null) {
          switch (type) {
            case TASK -> taskManager.updateTask(task, task.getIdTask());
            case EPIC -> taskManager.updateEpic((Epic) task, task.getIdTask());
            case SUBTASK -> taskManager.updateSubtask((Subtask) task, task.getIdTask());
          }
        } else {
          taskManager.createTask(task);
        }
        exchange.sendResponseHeaders(HttpCodeResponse.MODIFIED.getCode(), 0);
      } catch (IllegalArgumentException e) {
        System.out.println(e.getMessage());
        exchange.sendResponseHeaders(BAD_REQUEST.getCode(), 0);
        sendNotFound(exchange, e.getMessage());
      } catch (TaskTimeOverlapException e) {
        System.out.println(e.getMessage());
        sendHasOverLap(exchange);
      } catch (IOException e) {
        System.out.println(e.getMessage());
        exchange.sendResponseHeaders(SERVER_ERROR.getCode(), 0);
      } catch (NotFoundException e) {
        System.out.println(e.getMessage());
        sendNotFound(exchange, e.getMessage());
      }
    }
  }

  protected void deleteByRequest(HttpExchange exchange) throws IOException {
      var path = exchange.getRequestURI().getPath();
        if (Pattern.matches("^/tasks$", path)) {
            taskManager.removeAllTypesOfTasks();
            exchange.sendResponseHeaders(HttpCodeResponse.OK.getCode(), 0);
            return;
        } if (Pattern.matches("^/tasks/\\d+"))
  }
}
