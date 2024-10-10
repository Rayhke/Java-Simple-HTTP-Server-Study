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

    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        Socket socket = Mockito.mock(Socket.class);
        httpResponse = new HttpResponseImpl(socket);
    }

    @Test
    @DisplayName("Socket is null")
    void constructor() {
        //TODO#106 socket null check, IllegalArgumentException이 발생 하는지 검증 합니다.

    }

    @Test
    @DisplayName("instance of PrintWriter")
    void getWriter() throws IOException {
        Assertions.assertInstanceOf(PrintWriter.class, httpResponse.getWriter());
    }

    @Test
    void setCharacterEncoding() {
        httpResponse.setCharacterEncoding("euc-kr");
        Assertions.assertEquals("euc-kr", httpResponse.getCharacterEncoding());
    }

    @Test
    @DisplayName("default Character Encoding : utf-8")
    void getCharacterEncoding() {
        //TODO#107 default getCharacterEncoding()이 'utf-8'인지 검증 합니다.

    }
}