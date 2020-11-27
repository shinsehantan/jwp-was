package controller;

import java.util.Optional;
import model.general.ContentType;
import model.general.Header;
import model.general.Headers;
import model.general.Method;
import model.general.Status;
import model.request.HttpRequest;
import model.response.HttpResponse;
import model.response.StatusLine;
import utils.FileIoUtils;

public class ResourceController extends AbstractController {

    private static final String TEMPLATE_LOCATION = "templates";
    private static final String STATIC_LOCATION = "static";

    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        Method method = httpRequest.getMethod();

        if (method.equals(Method.GET)) {
            return doGet(httpRequest);
        }

        return super.service(httpRequest);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        Optional<ContentType> contentType = httpRequest.extractContentType();

        if (contentType.isPresent()) {
            return findResourceFile(httpRequest);
        }
        if (httpRequest.whetherUriHasExtension()) {
            return HttpResponse.of(Status.NOT_FOUND);
        }

        return super.doGet(httpRequest);
    }

    private HttpResponse findResourceFile(HttpRequest httpRequest) {
        Optional<ContentType> contentType = httpRequest.extractContentType();
        String requestUri = httpRequest.getRequestUri();

        if (!contentType.isPresent()) {
            return HttpResponse.of(Status.NOT_FOUND);
        }

        byte[] body;
        try {
            body = FileIoUtils
                .loadFileFromClasspath(generatePath(contentType.get(), requestUri));
        } catch (Exception e) {
            return HttpResponse.of(Status.NOT_FOUND);
        }
        StatusLine statusLine = StatusLine.of(httpRequest.getHttpVersion(), Status.OK);
        Headers headers = new Headers();
        headers.addHeader(Header.CONTENT_TYPE, contentType.get()
            .getContentTypeValue());
        headers.addHeader(Header.CONTENT_LENGTH, String.valueOf(body.length));

        return HttpResponse.of(statusLine, headers, body);
    }

    private static String generatePath(ContentType contentType, String requestUri) {
        if (contentType.equals(ContentType.HTML) || contentType.equals(ContentType.ICO)) {
            return TEMPLATE_LOCATION + requestUri;
        }

        return STATIC_LOCATION + requestUri;
    }
}
