package http.request;

import exception.InvalidHttpMessageException;
import exception.InvalidRequestLineException;
import http.StartLine;
import utils.StringUtils;

public class RequestLine implements StartLine {
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_ELEMENT_COUNT = 3;

    private final HttpMethod httpMethod;
    private final HttpUri httpUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, HttpUri httpUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.httpUri = httpUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String line) {
        StringUtils.validateNonNullAndNotEmpty(() -> new InvalidHttpMessageException(line), line);

        String[] tokens = createTokens(line);

        HttpMethod httpMethod = HttpMethod.from(tokens[0]);
        HttpUri httpUri = HttpUri.from(tokens[1]);
        HttpVersion httpVersion = new HttpVersion(tokens[2]);

        return new RequestLine(httpMethod, httpUri, httpVersion);
    }

    private static String[] createTokens(String line) {
        String[] tokens = line.split(REQUEST_LINE_DELIMITER);
        if (tokens.length != REQUEST_LINE_ELEMENT_COUNT) {
            throw new InvalidRequestLineException(line);
        }

        return tokens;
    }

    public HttpUri getHttpUri() {
        return httpUri;
    }

    @Override
    public String toHttpMessage() {
        return httpMethod.name() + REQUEST_LINE_DELIMITER
                + httpUri.toHttpMessage() + REQUEST_LINE_DELIMITER
                + httpVersion.toHttpMessage();
    }
}
