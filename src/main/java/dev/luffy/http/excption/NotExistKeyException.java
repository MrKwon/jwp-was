package dev.luffy.http.excption;

public class NotExistKeyException extends RuntimeException {
    public NotExistKeyException(String message) {
        super(message);
    }
}
