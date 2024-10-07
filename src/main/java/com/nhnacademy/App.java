package com.nhnacademy;

import com.nhnacademy.http.SimpleHttpServer;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer(8080);
        simpleHttpServer.start();
    }
}
