package webserver.controller;

import db.DataBase;
import utils.TemplateFactory;
import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import webserver.http.session.HttpSessionStorage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class UserListController extends AbstractController {
    private static final String USER_LIST_PATH = "user/list";
    private static final String REDIRECT_PATH = "/user/login.html";

    public UserListController() {
        this.paths = Collections.singletonList("/user/list");
    }

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        try {
            List<String> jSessionCookies = httpRequest.getJSessionCookies();
            if (HttpSessionStorage.isValidSession(jSessionCookies)) {
                String template = TemplateFactory.apply(USER_LIST_PATH, DataBase.findAll());
                byte[] body = template.getBytes();
                httpResponse.forward(body);
                return;
            }
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            httpResponse.sendRedirect(REDIRECT_PATH);
        }
    }
}
