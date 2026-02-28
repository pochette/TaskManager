package server;

import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    public static final String DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(("HH:mm dd.MM.yyyy"), java.util.Locale.getDefault()).toString();
}
