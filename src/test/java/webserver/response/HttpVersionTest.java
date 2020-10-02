package webserver.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import exception.NotFoundHttpVersionException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class HttpVersionTest {

    @DisplayName("HttpVersion 찾기 - 성공")
    @ParameterizedTest
    @MethodSource("provideHttpVersionForFind")
    void find(String input, HttpVersion httpVersion) {
        assertThat(HttpVersion.find(input)).isEqualTo(httpVersion);
    }

    @DisplayName("StatusCode 찾기 - 예외, 해당하는 코드를 찾지 못함")
    @Test
    void find_NotExistStatusCode_ThrownException() {
        String httpVersion = "HTTP/100";
        assertThatThrownBy(() -> HttpVersion.find(httpVersion))
            .isInstanceOf(NotFoundHttpVersionException.class);
    }

    private static Stream<Arguments> provideHttpVersionForFind() {
        return Stream.of(
            Arguments.of("HTTP/0.9", HttpVersion.HTTP09),
            Arguments.of("HTTP/1.0", HttpVersion.HTTP10),
            Arguments.of("HTTP/1.1", HttpVersion.HTTP11),
            Arguments.of("HTTP/2", HttpVersion.HTTP2)
        );
    }
}
