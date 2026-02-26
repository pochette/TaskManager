public enum HTTPCodeResponse {
    OK(200),
    MODIFIED(201),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    OVERLAP(406),
    SERVER_ERROR(500);
    private final int code;

    HTTPCodeResponse(int code) {
        this.code = code;
    }
public int getCode() { return code;}
}
