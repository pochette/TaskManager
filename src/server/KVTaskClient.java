package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private static final String requestTemplate = "%s,%s,%s&API_KEY=%s";
    private final HttpClient client;
    private final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().header("Content-Type", "application/json");
    private final String url;
    private String apiKey;

    public KVTaskClient(String url) {
        this.client = HttpClient.newHttpClient();
        this.url = url;
    }

    private String registerUrl() {
        return String.format("%s/register", this.url);
    }

    private String saveUrl(String url, String key) {
        if(apiKey == null) {
            throw new IllegalStateException("Call server.KVTaskClient.register() before saving data");
        }
        return String.format("%s/save/%s", this.url, key);
    }
    private String loadUrl(String key) {
        if (apiKey==null){
            throw new IllegalStateException("Call server.KVTaskClient.register() before loading data");
        }
        return String.format(requestTemplate,this.url, "load", key, apiKey);
    }
    public void register() {
        final var request = HttpRequest.newBuilder().uri(URI.create(registerUrl())).GET().build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Failed to register server.KVTaskClient");
        }
    }
}
