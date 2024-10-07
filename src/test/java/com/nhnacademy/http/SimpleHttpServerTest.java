package com.nhnacademy.http;

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
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SimpleHttpServerTest {

    private static final int TEST_PORT = 9999;

    @BeforeAll
    static void beforeAllSetUp() {
        Thread thread = new Thread(() -> {
            SimpleHttpServer simpleHttpServer = new SimpleHttpServer(TEST_PORT);
            try {
                simpleHttpServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                log.debug("exit!!!");
            }
        });
        thread.start();
    }

    @DisplayName("status code : 200 ok")
    @Test
    void request1() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                            .uri(new URI(String.format("http://localhost:%d", TEST_PORT)))
                                            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.debug("response : {}", response.body());

        // response.statusCode() == 200 검증 합니다.
        Assertions.assertEquals(200, response.statusCode());
    }

    @DisplayName("response: hello java")
    @Test
    void request2() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                            .uri(new URI(String.format("http://localhost:%d", TEST_PORT)))
                                            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // response.body() 'Hello' or 'Java' 문자열이 포함되었는지 검증 합니다.
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.body().contains("Hello")),
                () -> Assertions.assertTrue(response.body().contains("Java"))
        );
    }

    @DisplayName("content-type")
    @Test
    void request3() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                            .uri(new URI(String.format("http://localhost:%d", TEST_PORT)))
                                            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Optional<String> contentTypeOptional = response.headers().firstValue("Content-Type");
        String actual = contentTypeOptional.get().toLowerCase();
        log.debug("contentType : {}", actual);

        // contentType 이 'text/html' 검증 합니다.
        Assertions.assertTrue(actual.contains("text/html"));

    }

    @DisplayName("charset utf-8")
    @Test
    void request4() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("http://localhost:%d", TEST_PORT)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Optional<String> contentTypeOptional = response.headers().firstValue("Content-Type");
        String actual = contentTypeOptional.get().toLowerCase();
        log.debug("contentType : {}", actual);

        // contentType header 의 charset=utf-8 인지 검증 합니다.
        Assertions.assertTrue(actual.contains("utf-8"));

    }

    @DisplayName("Content-Length")
    @Test
    void request5() throws URISyntaxException, IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("http://localhost:%d", TEST_PORT)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Optional<String> contentLengthOptional = response.headers().firstValue("Content-Length");
        String actual = contentLengthOptional.get();

        log.debug("Content-Length : {}", actual);

        // content-Length 값이 존재하는지 검증 합니다.
        Assertions.assertTrue(Objects.nonNull(actual) && !actual.isBlank());
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        Thread.sleep(1000);
    }
}