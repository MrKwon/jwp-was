package dev.luffy.http;

import java.util.HashMap;
import java.util.Map;

import dev.luffy.http.excption.NotFoundCookieException;

public class HttpCookie {

    private static final String NOT_FOUND_COOKIE_MESSAGE = "그런 쿠키가 없어요.";

    private Map<String, String> cookie;

    public HttpCookie() {
        cookie = new HashMap<>();
    }

    public void put(String key, String value) {
        cookie.put(key, value);
    }

    public String get(String key) {
        if (!cookie.containsKey(key)) {
            throw new NotFoundCookieException(NOT_FOUND_COOKIE_MESSAGE);
        }
        return cookie.get(key);
    }

    public void addCookies(Map<String, String> cookie) {
        cookie.forEach(this.cookie::put);
    }

    @Override
    public String toString() {
        return "HttpCookie{" +
                "cookie=" + cookie +
                '}';
    }
}
