package model.general;

import static constants.CookieConstants.LOGIN_CHECK_COOKIE_KEY;

import java.util.List;
import java.util.Optional;

public class Cookies {

    private final List<Cookie> cookies;

    public Cookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public Optional<Cookie> getCookie(String key) {
        return cookies.stream()
            .filter(c -> c.isSameKey(key))
            .findFirst();
    }

    public boolean isLogined() {
        Optional<Cookie> loginedCookie = getCookie(LOGIN_CHECK_COOKIE_KEY);

        if (!loginedCookie.isPresent()) {
            return false;
        }

        Cookie cookie = loginedCookie.get();

        return new Boolean(cookie.getValue());
    }
}
