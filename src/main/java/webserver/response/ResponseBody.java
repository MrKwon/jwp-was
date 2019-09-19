package webserver.response;

import java.io.IOException;
import java.net.URISyntaxException;

import utils.FileIoUtils;

public class ResponseBody {
    private byte[] body;

    public ResponseBody(String path) throws IOException, URISyntaxException {
        this.body = FileIoUtils.loadFileFromClasspath(path);
    }

    public int getLength() {
        return body.length;
    }

    public byte[] getBody() {
        return body;
    }
}
