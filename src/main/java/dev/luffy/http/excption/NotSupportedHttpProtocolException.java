package dev.luffy.http.excption;

public class NotSupportedHttpProtocolException extends RuntimeException {
    public NotSupportedHttpProtocolException(String message) {
        super(message);
    }
}
