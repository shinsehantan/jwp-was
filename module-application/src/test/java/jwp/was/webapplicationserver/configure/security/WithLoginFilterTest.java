package jwp.was.webapplicationserver.configure.security;

import static com.google.common.net.HttpHeaders.COOKIE;
import static com.google.common.net.HttpHeaders.LOCATION;
import static jwp.was.util.Constants.ATTRIBUTE_KEY_USER;
import static jwp.was.util.Constants.HEADERS_EMPTY;
import static jwp.was.util.Constants.HTTP_VERSION;
import static jwp.was.util.Constants.PARAMETERS_EMPTY;
import static jwp.was.util.Constants.SET_COOKIE_SESSION_ID_KEY;
import static jwp.was.util.Constants.URL_PATH_LOGIN_HTML;
import static jwp.was.util.Constants.URL_PATH_PAGE_API_USER_LIST;
import static jwp.was.util.Constants.USER_EMAIL;
import static jwp.was.util.Constants.USER_ID;
import static jwp.was.util.Constants.USER_NAME;
import static jwp.was.util.Constants.USER_PASSWORD;
import static jwp.was.util.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import jwp.was.dto.Headers;
import jwp.was.dto.HttpRequest;
import jwp.was.dto.HttpResponse;
import jwp.was.dto.UrlPath;
import jwp.was.util.HttpMethod;
import jwp.was.util.HttpStatusCode;
import jwp.was.webapplicationserver.configure.controller.info.HttpInfo;
import jwp.was.webapplicationserver.configure.session.HttpSession;
import jwp.was.webapplicationserver.configure.session.HttpSessions;
import jwp.was.webapplicationserver.configure.session.UUIDBasedHttpSession;
import jwp.was.webapplicationserver.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WithLoginFilterTest {

    private static final WithLoginFilter LOGIN_CONFIGURE = WithLoginFilter.getInstance();

    @DisplayName("로그인 검증 - Void, 로그인해야하는 정보에 쿠키가 있음")
    @Test
    void verifyLogin_LoginInfoAndHasCookie_Nothing() {
        HttpMethod httpMethod = GET;
        UrlPath urlPath = URL_PATH_PAGE_API_USER_LIST;
        assertThat(LOGIN_CONFIGURE.getWithLoginInfo())
            .contains(HttpInfo.of(httpMethod, urlPath.getUrlPath()));

        HttpSessions httpSessions = HttpSessions.getInstance();
        HttpSession httpSession = new UUIDBasedHttpSession();
        User user = new User(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        httpSession.setAttribute(ATTRIBUTE_KEY_USER, user);
        httpSessions.saveSession(httpSession);

        Map<String, String> headers = new HashMap<>();
        headers.put(COOKIE, SET_COOKIE_SESSION_ID_KEY + httpSession.getId());

        HttpRequest httpRequest = new HttpRequest(
            httpMethod,
            urlPath,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            new Headers(headers)
        );

        LOGIN_CONFIGURE.validateLogin(httpRequest);

        httpSessions.removeSession(httpSession.getId());
    }

    @DisplayName("로그인 검증 - 예외 발생, User 속성이 없는 세션")
    @Test
    void verifyLogin_LoginInfoAndHasCookieNotExistsUser_ThrownNeedLoginException() {
        HttpMethod httpMethod = GET;
        UrlPath urlPath = URL_PATH_PAGE_API_USER_LIST;
        assertThat(LOGIN_CONFIGURE.getWithLoginInfo())
            .contains(HttpInfo.of(httpMethod, urlPath.getUrlPath()));

        HttpSessions httpSessions = HttpSessions.getInstance();
        HttpSession httpSession = new UUIDBasedHttpSession();
        httpSessions.saveSession(httpSession);

        Map<String, String> headers = new HashMap<>();
        headers.put(COOKIE, SET_COOKIE_SESSION_ID_KEY + httpSession.getId());

        HttpRequest httpRequest = new HttpRequest(
            httpMethod,
            urlPath,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            new Headers(headers)
        );

        assertThatThrownBy(() -> LOGIN_CONFIGURE.validateLogin(httpRequest))
            .isInstanceOf(NeedLoginException.class);

        httpSessions.removeSession(httpSession.getId());
    }

    @DisplayName("로그인 검증 - 예외 발생, 로그인해야하는 정보에 유효하지 않은 SessionIdCookie 포함 쿠키가 있음")
    @Test
    void verifyLogin_LoginInfoAndHasCookieWrongSessionId_ThrownNeedLoginException() {
        HttpMethod httpMethod = GET;
        UrlPath urlPath = URL_PATH_PAGE_API_USER_LIST;
        assertThat(LOGIN_CONFIGURE.getWithLoginInfo())
            .contains(HttpInfo.of(httpMethod, urlPath.getUrlPath()));

        Map<String, String> headers = new HashMap<>();
        headers.put(COOKIE, SET_COOKIE_SESSION_ID_KEY + "wrongSessionId");

        HttpRequest httpRequest = new HttpRequest(
            httpMethod,
            urlPath,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            new Headers(headers)
        );

        assertThatThrownBy(() -> LOGIN_CONFIGURE.validateLogin(httpRequest))
            .isInstanceOf(NeedLoginException.class);
    }

    @DisplayName("로그인 검증 - 예외 발생, 로그인해야하는 정보인데, 쿠키가 없음")
    @Test
    void verifyLogin_LoginInfoAndHasNotCookie_ThrownNeedLoginException() {
        HttpMethod httpMethod = GET;
        UrlPath urlPath = URL_PATH_PAGE_API_USER_LIST;
        assertThat(LOGIN_CONFIGURE.getWithLoginInfo())
            .contains(HttpInfo.of(httpMethod, urlPath.getUrlPath()));

        HttpRequest httpRequest = new HttpRequest(
            httpMethod,
            urlPath,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            HEADERS_EMPTY
        );

        assertThatThrownBy(() -> LOGIN_CONFIGURE.validateLogin(httpRequest))
            .isInstanceOf(NeedLoginException.class);
    }

    @DisplayName("로그인 검증 - Void, 로그인해야하는 정보가 아님")
    @Test
    void verifyLogin_NotLoginInfo_Nothing() {
        HttpMethod httpMethod = GET;
        UrlPath urlPath = UrlPath.from("/notLogin");
        assertThat(LOGIN_CONFIGURE.getWithLoginInfo())
            .doesNotContain(HttpInfo.of(httpMethod, urlPath.getUrlPath()));

        HttpRequest httpRequest = new HttpRequest(
            httpMethod,
            urlPath,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            HEADERS_EMPTY
        );

        LOGIN_CONFIGURE.validateLogin(httpRequest);
    }

    @DisplayName("LoginPage로 Redirect시키는 HttpResponse를 가져옴")
    @Test
    void getRedirectLoginPage() {
        HttpRequest httpRequest = new HttpRequest(
            GET,
            URL_PATH_PAGE_API_USER_LIST,
            PARAMETERS_EMPTY,
            HTTP_VERSION,
            HEADERS_EMPTY
        );

        HttpResponse redirectLoginPage = LOGIN_CONFIGURE.getRedirectLoginPage(httpRequest);

        assertThat(redirectLoginPage.getHttpVersion()).isEqualTo(httpRequest.getHttpVersion());
        assertThat(redirectLoginPage.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
        assertThat(redirectLoginPage.getHeaders().get(LOCATION)).isEqualTo(
            URL_PATH_LOGIN_HTML);
    }
}
