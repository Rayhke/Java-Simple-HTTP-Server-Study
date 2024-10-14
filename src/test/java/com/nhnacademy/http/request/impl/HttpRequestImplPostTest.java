package com.nhnacademy.http.request.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.util.StringUtils;
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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class HttpRequestImplPostTest {

    private static final long TEST_PORT = 35555;

    private static final String DEFAULT_METHOD_TYPE = "POST";

    private static final String DEFAULT_QUERY_STRING_ID_KEY = "id";

    private static final String DEFAULT_QUERY_STRING_ID_VALUE = "marco";

    private static final String DEFAULT_QUERY_STRING_NAME_KEY = "name";

    private static final String DEFAULT_QUERY_STRING_NAME_VALUE = "마르코";

    private static final String DEFAULT_QUERY_STRING_AGE_KEY = "age";

    private static final String DEFAULT_QUERY_STRING_AGE_VALUE = "40";

    private static final String DEFAULT_REQUEST_URL = "/index.html";

    private static final String DEFAULT_ADDRESS = "localhost";

    private static HttpRequest request;

    private static Socket client = Mockito.mock(Socket.class);

    @BeforeAll
    static void setUp() throws IOException {

        StringBuilder data = new StringBuilder();
        data.append(String.format("%s=%s", DEFAULT_QUERY_STRING_ID_KEY,
                URLEncoder.encode(DEFAULT_QUERY_STRING_ID_VALUE, StringUtils.DEFAULT_CHARSET)));
        data.append(String.format("&%s=%s", DEFAULT_QUERY_STRING_NAME_KEY,
                URLEncoder.encode(DEFAULT_QUERY_STRING_NAME_VALUE, StringUtils.DEFAULT_CHARSET)));
        data.append(String.format("&%s=%s", DEFAULT_QUERY_STRING_AGE_KEY,
                URLEncoder.encode(DEFAULT_QUERY_STRING_AGE_VALUE, StringUtils.DEFAULT_CHARSET)));
        log.debug("data : {}", data);

        // ====================================================================
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s HTTP/1.1%s", DEFAULT_METHOD_TYPE, DEFAULT_REQUEST_URL, StringUtils.CRLF));
        sb.append(String.format("Host: %s:%d%s", DEFAULT_ADDRESS, TEST_PORT, StringUtils.CRLF));
        sb.append(String.format("Content-Type: application/x-www-form-urlencoded; charset=%s%s", StringUtils.DEFAULT_CHARSET, StringUtils.CRLF));
        sb.append(String.format("Content-Length: %d%s", data.toString().getBytes(StringUtils.DEFAULT_CHARSET).length, StringUtils.CRLF));
        sb.append(StringUtils.CRLF);
        sb.append(data);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes(StringUtils.DEFAULT_CHARSET));
        Mockito.when(client.getInputStream()).thenReturn(inputStream);
        request = new HttpRequestImpl(client);
    }

    @DisplayName("getMethod() = POST")
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
        expected.put("name", "마르코");
        expected.put("age", "40");

        Map actual = request.getParameterMap();
        assertEquals(expected, actual);
    }

    @Test
    void getHeader_contentType() {
        String contentType = request.getHeader("Content-Type");
        log.debug("contentType : {}", contentType);
        Assertions.assertTrue(contentType.contains("application/x-www-form-urlencoded"));
    }

    // TODO : Content-Type 과 charset 데이터가 Header 요청의 동일한 줄에 있기 때문에, 이 또한 구분할 구조가 필요.
    @Test
    void getHeader_charset() {
        String charset = request.getHeader("charset");
        log.debug("charset : {}", charset);
        Assertions.assertTrue(charset.contains(StringUtils.DEFAULT_CHARSET.toString()));
    }

    @DisplayName("URI=/index.html")
    @Test
    void getRequestURI() {
        Assertions.assertEquals(DEFAULT_REQUEST_URL,
                request.getRequestURI());
    }
}
