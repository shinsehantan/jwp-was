package jwp.was.webserver.dto;

import com.google.common.net.HttpHeaders;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jwp.was.dto.Headers;
import jwp.was.dto.HttpRequest;
import jwp.was.dto.HttpVersion;
import jwp.was.dto.Parameters;
import jwp.was.dto.UrlPath;
import jwp.was.util.HttpMethod;
import jwp.was.webserver.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestAssembler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestAssembler.class);
    private static final String LINE_DELIMITER = " ";
    private static final String EMPTY = "";
    private static final int HTTP_METHOD_INDEX_OF_REQUEST_LINE = 0;
    private static final int URL_INDEX_OF_REQUEST_LINE = 1;
    private static final int HTTP_VERSION_INDEX_OF_REQUEST_LINE = 2;

    public static HttpRequest assemble(BufferedReader br) throws IOException {
        String line = readLine(br);
        String[] requestLine = line.split(LINE_DELIMITER);

        HttpMethod httpMethod = HttpMethod.from(requestLine[HTTP_METHOD_INDEX_OF_REQUEST_LINE]);
        UrlPath urlPath = UrlPath.from(requestLine[URL_INDEX_OF_REQUEST_LINE]);
        Parameters parameters = Parameters.fromUrl(requestLine[URL_INDEX_OF_REQUEST_LINE]);
        HttpVersion httpVersion = HttpVersion.of(requestLine[HTTP_VERSION_INDEX_OF_REQUEST_LINE]);
        Headers headers = Headers.from(readHeaders(br));

        if (httpMethod.hasRequestBody()) {
            String body = readBody(br, headers);
            parameters = Parameters.combine(parameters, Parameters.fromEncodedParameter(body));
        }

        return new HttpRequest(httpMethod, urlPath, parameters, httpVersion, headers);
    }

    private static String readLine(BufferedReader br) throws IOException {
        String line = br.readLine();
        LOGGER.debug("request line : {}", line);
        return line;
    }

    private static List<String> readHeaders(BufferedReader br) throws IOException {
        List<String> headers = new ArrayList<>();

        while (true) {
            String header = br.readLine();
            if (header.equals(EMPTY)) {
                break;
            }
            LOGGER.debug("header : {}", header);
            headers.add(header);
        }
        return headers;
    }

    private static String readBody(BufferedReader br, Headers headers) throws IOException {
        int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
        String body = IOUtils.readData(br, contentLength);
        LOGGER.debug("body : {}", body);
        return body;
    }
}
