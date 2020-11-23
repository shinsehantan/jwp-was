package jwp.was.webserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import jwp.was.dto.HttpRequest;
import jwp.was.webserver.dto.HttpRequestAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final DispenseHandler dispenseHandler;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.dispenseHandler = new DispenseHandler();
    }

    public void run() {
        LOGGER.debug(
            "New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort()
        );

        try (InputStream in = connection.getInputStream();
            OutputStream out = connection.getOutputStream();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            HttpRequest httpRequest = HttpRequestAssembler.assemble(br);
            dispenseHandler.dispense(out, httpRequest);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
