package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import com.google.gson.GsonBuilder;
import model.Task;

public class KVTaskClient implements TaskSerializer {
  private static final String requestTemplate =
      "%s,%s,%s&API_KEY=%s"; // url,method,key&API_KEY=apiKey
  private final HttpClient client;
  private final HttpRequest.Builder requestBuilder =
      HttpRequest.newBuilder().header("Content-Type", "application/json");
  private final String url;
  private final TaskSerializer taskSerializer;
  private String apiKey;
  private GsonBuilder gsonBuilder;

  public KVTaskClient(String url, TaskSerializer taskSerializer) {
    this.taskSerializer = taskSerializer;
    this.client = HttpClient.newHttpClient();
    this.url = url;
  }

  private GsonBuilder createGsonBuilder() {
      return new GsonBuilder()
          .registerTypeAdapter(Duration.class, new DurationAdapter())
          .registerTypeAdapter(DateTimeFormatAdapter.class, new DateTimeFormatAdapter())
  }

  @Override
  public Task fromJson(String json) {

  }

  @Override
  public String toJson(Task task) {
    return "";
  }

  /** Этот метод загружает задачу с KV сервера. */
  public Optional<Task> loadTask(String key) {
    final var request = requestBuilder
        .uri(URI.create(loadUrl(key)))
        .build();
    try {
      final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 404) {
        return Optional.empty();
      } else {
        return Optional.of(this.taskSerializer.fromJson(response.body()));
      }
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private String loadUrl(String key) {
    if (apiKey == null) {
      throw new IllegalStateException("Call server.KVTaskClient.register() before loading data");
    }
    return String.format(requestTemplate, this.url, "load", key, apiKey);
  }

  public void register() {
    final var request = HttpRequest.newBuilder().uri(URI.create(registerUrl())).GET().build();
    try {
      final var apiKey = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (apiKey == null || apiKey.body() == null || apiKey.body().isEmpty()) {
        throw new IllegalStateException("Failed to register server.KVTaskClient");
      }

    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Failed to register server.KVTaskClient");
    }
  }

  private String registerUrl() {
    return String.format("%s/register", this.url);
  }

  public void save(Task task) {
    final var taskJson = taskSerializer.toJson(task);
    final var request =
        HttpRequest.newBuilder()
            .uri(URI.create(saveUrl(task.getIdTask())))
            .POST(HttpRequest.BodyPublishers.ofString(taskJson))
            .build();
    try {
      client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      throw new IllegalArgumentException(
          String.format("Task with id %s is already exist", task.getIdTask()));
    }
  }

  private String saveUrl(String key) {
    if (apiKey == null) {
      throw new IllegalStateException("Call server.KVTaskClient.register() before saving data");
    }
    return String.format("%s/save/%s", this.url, key);
  }
}
