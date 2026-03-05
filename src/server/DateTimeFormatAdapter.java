package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatAdapter extends TypeAdapter<DateTimeFormatAdapter> {
    public static final String DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm dd.MM.yyyy"), java.util.Locale.getDefault()).toString();

    @Override
    public void write(JsonWriter jsonWriter, DateTimeFormatAdapter dateTimeFormat) throws IOException {

    }

    @Override
    public DateTimeFormatAdapter read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
