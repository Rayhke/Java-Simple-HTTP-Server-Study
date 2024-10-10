package com.nhnacademy.http.request.impl;

import com.nhnacademy.http.request.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HttpRequestImplTest {

    private static final String DEFAULT_METHOD_TYPE = "GET";

    private static final String DEFAULT_QUERY_STRING_ID_KEY = "id";

    private static final String DEFAULT_QUERY_STRING_ID_VALUE = "marco";

    private static final String DEFAULT_QUERY_STRING_NAME_KEY = "name";

    private static final String DEFAULT_QUERY_STRING_NAME_VALUE = "마르코";

    private static final String DEFAULT_QUERY_STRING_AGE_KEY = "age";

    private static final String DEFAULT_QUERY_STRING_AGE_VALUE = "40";

    private static final String DEFAULT_REQUEST_URL = "/index.html";

    static HttpRequest request;

    static Socket client = Mockito.mock(Socket.class);

    @BeforeAll
    static void setUp() throws IOException {

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("GET /index.html?id=marco&age=40&name=마르코 HTTP/1.1%s", System.lineSeparator()));
        sb.append(String.format("Host: localhost:8080%s", System.lineSeparator()));
        sb.append(String.format("Connection: keep-alive%s", System.lineSeparator()));
        sb.append(String.format("Cache-Control: max-age=0%s", System.lineSeparator()));
        sb.append(String.format("sec-ch-ua-platform: macOS%s", System.lineSeparator()));
        sb.append(String.format("User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36%s", System.lineSeparator()));
        sb.append(String.format("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"));

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Mockito.when(client.getInputStream()).thenReturn(inputStream);
        request = new HttpRequestImpl(client);
    }

    @Test
    void constructor() {
        assertInstanceOf(HttpRequest.class, request);
    }

    @DisplayName("getMethod() = GET")
    @Test
    void getMethod() {
        Assertions.assertEquals(DEFAULT_METHOD_TYPE, request.getMethod());
    }

    @DisplayName("getParameterById : id=marco")
    @Test
    void getParameterById() {
        Assertions.assertEquals(DEFAULT_QUERY_STRING_ID_VALUE,
                request.getParameter(DEFAULT_QUERY_STRING_ID_KEY));
    }

    @DisplayName("getParameterByName : name=마르코")
    @Test
    void getParameterByName() {
        Assertions.assertEquals(DEFAULT_QUERY_STRING_NAME_VALUE,
                request.getParameter(DEFAULT_QUERY_STRING_NAME_KEY));
    }

    @DisplayName("getParameterByAge : age=40")
    @Test
    void getParameterByAge() {
        Assertions.assertEquals(DEFAULT_QUERY_STRING_AGE_VALUE,
                request.getParameter(DEFAULT_QUERY_STRING_AGE_KEY));
    }

    @Test
    void getParameterMap() {
        Map<String, Object> expected = new HashMap<>();
        expected.put("id", "marco");
        expected.put("age", "40");
        expected.put("name", "마르코");

        Map actual = request.getParameterMap();
        assertEquals(expected, actual);
    }

    @Test
    void getHeader() {
        Assertions.assertAll(() -> {
            assertEquals(request.getHeader("sec-ch-ua-platform"), "macOS");
            assertTrue(request.getHeader("User-Agent").contains("Mozilla/5.0"));
            assertTrue(request.getHeader("Host").contains("localhost"));
        });
    }

    @Test
    void attributeTest() {
        request.setAttribute("numberList", List.of(1, 2, 3, 4, 5));
        request.setAttribute("count", 1L);
        request.setAttribute("name", "엔에이치엔아카데미");
        long actual = (long) request.getAttribute("count");
        String nhnacademy = (String) request.getAttribute("name");
        List<Integer> numberList = (List<Integer>) request.getAttribute("numberList");
        Assertions.assertAll(() -> {
            assertEquals(1l, actual);
            assertEquals("엔에이치엔아카데미", nhnacademy);
            assertEquals(List.of(1, 2, 3, 4, 5), numberList);
        });
    }

    @DisplayName("URI=/index.html")
    @Test
    void getRequestURI() {
        Assertions.assertEquals(DEFAULT_REQUEST_URL,
                request.getRequestURI());
    }
}