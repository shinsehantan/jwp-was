package http.was.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import http.was.exception.IllegalRequestException;
import http.was.utils.IOUtils;

public class RequestBody {
    private static final String URL_DELIMITER = "&";
    private static final String BODY_DELIMITER = "=";

    private String body;

    public RequestBody(BufferedReader br, Integer contentLength) throws IOException {
        body = IOUtils.readData(br, contentLength);
    }

    public Map<String, String> parseRequestBody() throws IllegalRequestException {
        Map<String, String> result = new HashMap<>();

        if ("".equals(body)) {
            return result;
        }

        String[] tokens = body.split(URL_DELIMITER);
        for (String token : tokens) {
            String[] value = token.split(BODY_DELIMITER);
            if (value.length != 2) {
                throw new IllegalRequestException("key, value 쌍이 완전하지 않습니다.");
            }
            result.put(value[0], value[1]);
        }
        return Collections.unmodifiableMap(result);
    }

    public String getBody() {
        return body;
    }
}
