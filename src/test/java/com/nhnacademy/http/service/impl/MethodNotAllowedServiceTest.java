package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.request.impl.HttpRequestImpl;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.response.impl.HttpResponseImpl;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MethodNotAllowedServiceTest {

    private static final String DEFAULT_URL = "/index.html";

    private HttpService httpService;

    private HttpRequest httpRequest;

    private HttpResponse httpResponse;

    private PrintWriter printWriter;

    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws IOException {
        httpService = new MethodNotAllowedService();

        httpRequest = Mockito.mock(HttpRequestImpl.class);
        Mockito.when(httpRequest.getRequestURI()).thenReturn(DEFAULT_URL);

        httpResponse = Mockito.mock(HttpResponseImpl.class);

        // StringWriter 를 사용하여 커스텀 버퍼 생성
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        Mockito.when(httpResponse.getWriter()).thenReturn(printWriter);

    }

    @Test
    @DisplayName("instance of HttpService")
    void constructor() {
        Assertions.assertInstanceOf(HttpService.class, new MethodNotAllowedService());
    }

    @Test
    @DisplayName("doGet : 405 method not allowed")
    void doGet() {
        Mockito.when(httpRequest.getMethod()).thenReturn("GET");

        httpService.service(httpRequest, httpResponse);
        String response = stringWriter.toString();

        log.debug("response : {}", response);
        Assertions.assertAll(
                () -> Assertions.assertTrue(response.contains(String.valueOf(ResponseUtils.HttpStatus.METHOD_NOT_FOUND.getCode()))),
                () -> Assertions.assertTrue(response.contains(String.valueOf(ResponseUtils.HttpStatus.METHOD_NOT_FOUND.getDescription())))
        );
    }
}