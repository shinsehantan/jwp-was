package user.service;

import http.was.webserver.WebServer;

public class UserApplication {
    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer("user.service.controller");
        webServer.start(args);
    }
}
