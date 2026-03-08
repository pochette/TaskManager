package server;

import com.google.gson.GsonBuilder;
import model.Task;

import java.time.Duration;
import java.util.Collection;

public class JsonTaskSerializer implements TaskSerializer {
    private final GsonBuilder gsonBuilder = createGsonBuilder();

    private GsonBuilder createGsonBuilder() {
        return new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(DateTimeFormatAdapter.class, new DateTimeFormatAdapter())
            .setPrettyPrinting();
    }

    @Override
    public Task fromJson(String json) {
        return gsonBuilder.create().fromJson(json, Task.class);
    }

    @Override
    public String toJson(Task task) {
        return gsonBuilder.create().toJson(task);
    }

    @Override
    public String toJson(Collection<? extends Task> tasks) {
        return gsonBuilder.create().toJson(tasks);
    }
}
