package com.nhnacademy.http.response.impl;

import com.nhnacademy.http.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HttpResponseImplTest {

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    private static HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        Socket socket = Mockito.mock(Socket.class);
        httpResponse = new HttpResponseImpl(socket);
    }

    @DisplayName("Socket is null")
    @Test
    void constructor() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HttpResponseImpl(null));
    }

    @DisplayName("instance of PrintWriter")
    @Test
    void getWriter() throws IOException {
        Assertions.assertInstanceOf(PrintWriter.class, httpResponse.getWriter());
    }

    @Test
    void setCharacterEncoding() {
        httpResponse.setCharacterEncoding("euc-kr");
        Assertions.assertEquals("euc-kr", httpResponse.getCharacterEncoding());
    }

    @DisplayName("default Character Encoding : utf-8")
    @Test
    void getCharacterEncoding() {
        Assertions.assertEquals(DEFAULT_CHARSET_NAME, httpResponse.getCharacterEncoding());
    }
}