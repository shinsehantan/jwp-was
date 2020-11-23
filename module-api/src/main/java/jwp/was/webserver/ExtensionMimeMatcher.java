package jwp.was.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ExtensionMimeMatcher {

    private static final String LINE_SEPARATOR = ";";
    private static final String CONTENT_DELIMITER = " ";
    private static final String REPEATED_CONTENT_DELIMITER_REGEX = CONTENT_DELIMITER + "+";
    private static final int MIME_INDEX = 0;
    private static final int EXTENSION_START_INDEX = MIME_INDEX + 1;
    private static final Map<String, String> EXTENSION_MIME_MATCHER;
    private static final String DEFAULT_MIME = "application/octet-stream";
    private static final String EXTENSION_DELIMITER = "\\.";

    static {
        String mimeContent = "";
        try {
            URI mimeTxtFileRoute = ClassLoader.getSystemResource("mime.txt").toURI();
            File mimeFile = new File(mimeTxtFileRoute);
            mimeContent = readMimeContent(mimeFile);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        EXTENSION_MIME_MATCHER = makeExtensionMimeMatcher(mimeContent);
    }

    private ExtensionMimeMatcher() {
    }

    private static String readMimeContent(File mimeFile) throws IOException {
        try (FileReader fileReader = new FileReader(mimeFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return readMimeFile(bufferedReader);
        }
    }

    private static String readMimeFile(BufferedReader bufferedReader) throws IOException {
        StringBuilder content = new StringBuilder();
        String redLine = bufferedReader.readLine();
        while (redLine != null) {
            content.append(redLine);
            redLine = bufferedReader.readLine();
        }
        return content.toString();
    }

    private static Map<String, String> makeExtensionMimeMatcher(String mimeContent) {
        Map<String, String> extensionMimeMatcher = new HashMap<>();
        String[] contents = mimeContent.split(LINE_SEPARATOR);
        for (String content : contents) {
            putExtensionMimeMatcher(extensionMimeMatcher, content);
        }
        return Collections.unmodifiableMap(extensionMimeMatcher);
    }

    private static void putExtensionMimeMatcher(Map<String, String> extensionMimeMatcher,
        String content) {
        String[] contents = content.trim()
            .replaceAll(REPEATED_CONTENT_DELIMITER_REGEX, CONTENT_DELIMITER)
            .split(CONTENT_DELIMITER);
        String mime = contents[MIME_INDEX];
        for (int i = EXTENSION_START_INDEX; i < contents.length; i++) {
            String extension = contents[i];
            extensionMimeMatcher.put(extension, mime);
        }
    }

    public static String getMimeType(String urlPath) {
        String[] splitUrlPath = urlPath.split(EXTENSION_DELIMITER);
        if (splitUrlPath.length == 1) {
            throw new IllegalArgumentException("확장자가 없습니다.");
        }
        String extension = splitUrlPath[splitUrlPath.length - 1];
        String mimeType = EXTENSION_MIME_MATCHER.get(extension);
        if (Objects.isNull(mimeType)) {
            return DEFAULT_MIME;
        }
        return mimeType;
    }
}
