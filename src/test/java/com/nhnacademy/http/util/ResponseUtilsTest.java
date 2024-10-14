package com.nhnacademy.http.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ResponseUtilsTest {

    @DisplayName("isExist:/404.html")
    @Test
    void isExist1() {
        boolean actual = ResponseUtils.isExist(ResponseUtils.DEFAULT_404);
        Assertions.assertTrue(actual);
    }

    @DisplayName("isExist:/")
    @Test
    void isExist2() {
        boolean actual = ResponseUtils.isExist("/");
        Assertions.assertFalse(actual);
    }

    @DisplayName("isExist:/favicon.ico")
    @Test
    void isExist3() {
        boolean actual = ResponseUtils.isExist("/favicon.ico");
        Assertions.assertFalse(actual);
    }

    @DisplayName("tryGetBodyFromFile : /index.html")
    @Test
    void tryGetBodyFromFile() throws IOException {
        String actual = ResponseUtils.tryGetBodyFromFile("/index.html");
        Assertions.assertAll(
                () -> Assertions.assertTrue(actual.contains("<head>")),
                () -> Assertions.assertTrue(actual.contains("Hello")),
                () -> Assertions.assertTrue(actual.contains("Java")),
                () -> Assertions.assertTrue(actual.contains("</html>"))
        );
    }

    @DisplayName("createResponseHeader : 200")
    @Test
    void createResponseHeader1() {
        String actual = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.OK.getCode(), StringUtils.DEFAULT_CHARSET, 100);
        log.debug("actual : {}", actual);

        Assertions.assertAll(
                () -> Assertions.assertTrue(actual.contains("200")),
                () -> Assertions.assertTrue(actual.contains("OK")),
                () -> Assertions.assertTrue(actual.contains("charset=UTF-8"))
        );
    }

    @DisplayName("createResponseHeader : 404")
    @Test
    void createResponseHeader2() {
        String actual = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.NOT_FOUND.getCode(), StringUtils.DEFAULT_CHARSET, 100);
        log.debug("actual : {}", actual);

        Assertions.assertAll(
                () -> Assertions.assertTrue(actual.contains("404")),
                () -> Assertions.assertTrue(actual.contains("Not Found")),
                () -> Assertions.assertTrue(actual.contains("charset=UTF-8"))
        );
    }
}