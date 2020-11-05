package http.request;

import http.SessionContainer;
import http.factory.HttpRequestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @DisplayName("요청의 쿠키에 특정 값이 있는지 확인 기능 테스트 - sessionId가 있는 경우")
    @Test
    void containsKeyInCookie() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_Session_Id.txt"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequestFactory.createRequest(reader);

        assertThat(request.containsKeyInCookie(SessionContainer.SESSION_KEY_FOR_COOKIE)).isTrue();

    }

    @DisplayName("요청의 쿠키에 특정 값이 있는지 확인 기능 테스트 - sessionId가 없는 경우")
    @Test
    void containsKeyInCookieWhenNoContent() throws IOException {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_No_Session_Id.txt"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        HttpRequest request = HttpRequestFactory.createRequest(reader);

        assertThat(request.containsKeyInCookie(SessionContainer.SESSION_KEY_FOR_COOKIE)).isFalse();
    }
}
