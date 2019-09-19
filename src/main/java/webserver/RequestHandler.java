package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;

import model.User;
import utils.FileIoUtils;
import webserver.request.Request;
import webserver.response.CustomResponse;
import webserver.response.MediaType;
import webserver.response.ResponseBody;
import webserver.response.ResponseHeaders;
import webserver.response.ResponseStatus;

public class RequestHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            Request request = Request.of(in);

            DataOutputStream dos = new DataOutputStream(out);

            String path = request.getPath();
            int index = path.lastIndexOf(".");

            String extension = path.substring(index + 1);
            if (index != -1 && !extension.equals("html") && request.isGet()) {
                ResponseHeaders responseHeaders = new ResponseHeaders();
                ResponseBody responseBody = new ResponseBody(path);
                CustomResponse customResponse = new CustomResponse(ResponseStatus.OK, responseHeaders, responseBody);
                customResponse.doGet(dos, extension);
            }

            if (path.equals("/index.html") && request.isGet()) {
                get(dos, path, index);
            }


            if (path.equals("/user/form.html") && request.isGet()) {
                get(dos, path, index);
            }

            if (path.equals("/user/create") && request.isPost()) {
                User user = new User(
                        request.getBody("userId"),
                        request.getBody("password"),
                        request.getBody("name"),
                        request.getBody("email")
                );

                logger.debug("user : {}", user);

                ResponseHeaders responseHeaders = new ResponseHeaders();

                responseHeaders.put("status", ResponseStatus.FOUND);
                responseHeaders.put("Location", "/index.html");

                CustomResponse.response(responseHeaders, null, dos);
                return;
            }

        } catch (IOException | URISyntaxException e) {
            logger.error(e.getMessage());
        }
    }

    private void get(DataOutputStream dos, String path, int index) throws IOException, URISyntaxException {
        byte[] body = FileIoUtils.loadFileFromClasspath(path);

        ResponseHeaders responseHeaders = new ResponseHeaders();

        responseHeaders.put("status", ResponseStatus.OK);
        responseHeaders.put("Content-Type", MediaType.of(path.substring(index + 1)).getMediaType());
        responseHeaders.put("Content-Length", body.length);

        CustomResponse.response(responseHeaders, body, dos);

        return;
    }
}
