package http;

import java.util.Arrays;

public enum HttpRequestMethod {
    GET("GET"),
    POST("POST");

    private String method;

    HttpRequestMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public static HttpRequestMethod of(String method) {
        return Arrays.stream(values())
                .filter(requestMethod -> requestMethod.contains(method))
                .findFirst().orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 요청 메서드 입니다."));
    }

    private boolean contains(String method) {
        return this.method.contains(method);
    }

}
