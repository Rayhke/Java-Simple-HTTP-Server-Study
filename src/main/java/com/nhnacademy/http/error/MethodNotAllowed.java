package com.nhnacademy.http.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodNotAllowed extends RuntimeException {

    private static final String ERROR_MESSAGE = "405 - Method Not Allowed";

    private static final String MESSAGE = "잘못된 접근입니다.";

    public MethodNotAllowed(String url, String method) {
        super(String.format("%s -> [URL : %s | Method : %s]",
                                MESSAGE, url, method));
        log.error("{}", ERROR_MESSAGE);
    }
}
