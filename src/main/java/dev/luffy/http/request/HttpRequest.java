package dev.luffy.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.luffy.http.HttpCookie;
import dev.luffy.http.HttpProtocol;
import dev.luffy.http.excption.NotFoundCookieException;
import dev.luffy.http.excption.NotSupportedHttpRequestException;
import dev.luffy.utils.*;

public class HttpRequest {

    private static final String EMPTY_STRING = "";
    private static final String NOT_SUPPORTED_HTTP_REQUEST_MESSAGE = "지원하지 않는 요청입니다.";

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestParam httpRequestParam;
    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;
    private final HttpCookie httpCookie;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        HttpRequestLine httpRequestLine = getHttpRequestLine(bufferedReader);
        HttpRequestParam httpRequestParam = getHttpRequestParams(httpRequestLine);
        HttpRequestHeader httpRequestHeader = getHttpRequestHeader(bufferedReader);
        HttpRequestBody httpRequestBody = getHttpRequestBody(bufferedReader, httpRequestHeader.getContentLength());

        this.httpRequestLine = httpRequestLine;
        this.httpRequestParam = httpRequestParam;
        this.httpRequestHeader = httpRequestHeader;
        this.httpRequestBody = httpRequestBody;
        this.httpCookie = new HttpCookie();

        addRequestCookies();
    }

    private HttpRequestLine getHttpRequestLine(BufferedReader bufferedReader) throws IOException {
        String firstLine = bufferedReader.readLine();
        return new HttpRequestLine(firstLine);
    }

    private HttpRequestParam getHttpRequestParams(HttpRequestLine httpRequestLine) {
        Map<String, String> params = UrlParameterParser.parse(httpRequestLine.getUrl());
        return new HttpRequestParam(params);
    }

    private HttpRequestHeader getHttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!EMPTY_STRING.equals(line) && line != null) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        checkEmpty(lines);
        Map<String, String> headers = HeaderParser.parse(lines);
        return new HttpRequestHeader(headers);
    }

    private void checkEmpty(List<String> lines) {
        if (lines.isEmpty()) {
            throw new NotSupportedHttpRequestException(NOT_SUPPORTED_HTTP_REQUEST_MESSAGE);
        }
    }

    private HttpRequestBody getHttpRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        String bodyData = IOUtils.readData(bufferedReader, contentLength);
        Map<String, String> body = BodyDataParser.parse(bodyData);
        return new HttpRequestBody(body);
    }

    private boolean hasCookie() {
        return httpRequestHeader.hasCookie();
    }

    private void addRequestCookies() {
        if (hasCookie()) {
            this.httpCookie.addCookies(CookieParser.parse(this.httpRequestHeader.get("Cookie")));
        }
    }

    public boolean isGet() {
        return HttpRequestMethod.GET.equals(getMethod());
    }

    public boolean isPost() {
        return HttpRequestMethod.POST.equals(getMethod());
    }

    public HttpRequestMethod getMethod() {
        return httpRequestLine.getMethod();
    }

    public HttpProtocol getProtocol() {
        return httpRequestLine.getProtocol();
    }

    public String getPath() {
        return httpRequestLine.getPath();
    }

    public String getHeader(String header) {
        return httpRequestHeader.get(header);
    }

    public String getQueryParameter(String parameter) {
        return httpRequestParam.get(parameter);
    }

    public String getBodyParameter(String parameter) {
        return httpRequestBody.get(parameter);
    }

    public String getCookie(String key) {
        try {
            return httpCookie.get(key);
        } catch (NotFoundCookieException e) {
            return "";
        }
    }

    public boolean isLoggedIn() {
        return getCookie("logined").equals("true");
    }

    public boolean isStaticRequest() {
        return httpRequestLine.isStaticContent();
    }

    public String pathExtension() {
        return httpRequestLine.getExtension();
    }

    @Override
    public String toString() {
        return "HttpRequest{" + "\n" +
                "httpRequestLine=" + httpRequestLine + "\n" +
                ", httpRequestParam=" + httpRequestParam + "\n" +
                ", httpRequestHeader=" + httpRequestHeader + "\n" +
                ", httpRequestBody=" + httpRequestBody + "\n" +
                '}';
    }
}
