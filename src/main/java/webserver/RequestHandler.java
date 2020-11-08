package webserver;

import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final DispatcherServlet dispatcherServlet;

    public RequestHandler(Socket connectionSocket, DispatcherServlet dispatcherServlet) {
        this.connection = connectionSocket;
        this.dispatcherServlet = dispatcherServlet;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        logger.debug("thread information: " + Thread.currentThread().getName());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = new HttpRequest(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            dispatcherServlet.process(httpRequest, httpResponse);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
