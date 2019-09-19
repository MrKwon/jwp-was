package webserver.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class CustomResponse {

    private static final Logger logger = LoggerFactory.getLogger(CustomResponse.class);

    private ResponseStatus responseStatus;
    private ResponseHeaders responseHeaders;
    private ResponseBody responseBody;

    public CustomResponse() {
    }

    public CustomResponse(ResponseStatus responseStatus, ResponseHeaders responseHeaders, ResponseBody responseBody) {
        this.responseStatus = responseStatus;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public void doGet(DataOutputStream dos, String extension) {
        responseHeaders.put("status", ResponseStatus.OK);
        responseHeaders.put("Content-Type", MediaType.of(extension).getMediaType());
        responseHeaders.put("Content-Length", responseBody.getLength());

        response111(dos);

        return;
    }

    public void response111(DataOutputStream out) {
        if (responseHeaders.get("status").equals(ResponseStatus.OK)) {
            responseHeader(out);
            responseBody111(out);
        }
        if (responseHeaders.get("status").equals(ResponseStatus.FOUND)) {
            responseHeader(out);
        }
    }

    public static void response(ResponseHeaders responseHeaders, byte[] body, DataOutputStream out) {
        CustomResponse customResponse = new CustomResponse();
        if (responseHeaders.get("status").equals(ResponseStatus.OK)) {
            customResponse.responseHeader(responseHeaders, out);
            customResponse.responseBody(out, body);
        }
        if (responseHeaders.get("status").equals(ResponseStatus.FOUND)) {
            customResponse.responseHeader(responseHeaders, out);
        }
    }

    private void responseHeader(ResponseHeaders responseHeaders, DataOutputStream dos) {
        try {
            ResponseStatus status = (ResponseStatus) responseHeaders.get("status");
            dos.writeBytes(String.format("HTTP/1.1 %d %s\r\n", status.getCode(), status.name()));
            for (String key : responseHeaders.keySet()) {
                dos.writeBytes(String.format("%s: %s\r\n", key, responseHeaders.get(key)));
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos) {
        try {
            ResponseStatus status = (ResponseStatus) responseHeaders.get("status");
            dos.writeBytes(String.format("HTTP/1.1 %d %s\r\n", status.getCode(), status.name()));
            for (String key : responseHeaders.keySet()) {
                dos.writeBytes(String.format("%s: %s\r\n", key, responseHeaders.get(key)));
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody111(DataOutputStream dos) {
        try {
            dos.write(responseBody.getBody(), 0, responseBody.getLength());
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
