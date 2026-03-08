package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import model.Epic;
import model.Subtask;
import model.Task;
import service.NotFoundException;
import service.TaskTimeOverlapException;

public class EpicHandler extends TaskHandler implements HttpHandler {
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    super.handle(exchange);
  }

  @Override
  protected void getByRequest(HttpExchange exchange) throws IOException, NotFoundException {
    var path = exchange.getRequestURI().getPath();
    var id = parseTaskId(path);
    if (Pattern.matches("^/epic$", path)) sendEpicListByRequest(exchange);
    if (Pattern.matches("^/epic/\\d+$", path)) sendEpicByRequest(exchange, id);
    if (Pattern.matches("^epic/\\d+/subtask$", path)) sendSubtaskListByEpic(exchange, id);
    else {
      sendNotFound(exchange, "Epic by ID " + id + "has not found");
    }
  }

  @Override
  protected void postByRequest(HttpExchange exchange) throws IOException, TaskTimeOverlapException {
    var path = exchange.getRequestURI().getPath();
    var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    Epic epic = (Epic) taskSerializer.fromJson(body);
    if (Pattern.matches("^epic/\\d+$", path)) {}
  }

  @Override
  protected void deleteByRequest(HttpExchange exchange) throws IOException {
    super.deleteByRequest(exchange);
  }

  private void sendEpicListByRequest(HttpExchange exchange) throws IOException {
    List<Epic> epicList = taskManager.getEpicList();
    String response = taskSerializer.toJson(epicList);
    sendResponse(exchange, response);
  }

  private void sendEpicByRequest(HttpExchange exchange, Integer id) throws IOException {
    Task task = taskManager.getTaskById(id);

    if (task != null) {
      Epic epic = (Epic) task;
      sendResponse(exchange, taskSerializer.toJson(epic));
    } else {
      sendNotFound(exchange, "Epic with ID " + id + " not found");
    }
  }

  private void sendSubtaskListByEpic(HttpExchange exchange, Integer id) throws IOException {
    List<Subtask> subtaskByEpic = taskManager.getSubtasksOfEpic(id);
    var response = taskSerializer.toJson(subtaskByEpic);
    sendResponse(exchange, response);
  }
}
