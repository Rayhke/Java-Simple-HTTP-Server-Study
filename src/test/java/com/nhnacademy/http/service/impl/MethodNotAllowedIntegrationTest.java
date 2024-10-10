package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.SimpleHttpServer;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
class MethodNotAllowedIntegrationTest {

    private static final int TEST_PORT = 9999;

    private static SimpleHttpServer simpleHttpServer;

    private static Thread thread;

    @BeforeAll
    static void setUp() {
        thread = new Thread(() -> {
            simpleHttpServer = new SimpleHttpServer(TEST_PORT);
            try {
                simpleHttpServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
    }


    @DisplayName("doPost : 405 method not allowed, /index.html")
    @Test
    void doPost1() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = String.format("http://localhost:%d/index.html", TEST_PORT);
        log.debug("url : {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("response : {}", response.body());
        Assertions.assertEquals(ResponseUtils.HttpStatus.METHOD_NOT_FOUND.getCode(), response.statusCode());
    }

    @DisplayName("doPost : 405 method not allowed, /info.html")
    @Test
    void doPost2() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = String.format("http://localhost:%d/info.html", TEST_PORT);
        log.debug("url : {}", url);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        log.debug("response : {}", response.body());
        Assertions.assertEquals(ResponseUtils.HttpStatus.METHOD_NOT_FOUND.getCode(), response.statusCode());
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        Thread.sleep(2000);
    }
}
