package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RegisterHttpService implements HttpService {

    public static final String URL = "/register.html";

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // Body - 설정
        String responseBody = ResponseUtils.tryGetBodyFromFile(httpRequest.getRequestURI());

        // Header - 설정
        String responseHeader = null;

        // PrintWriter 응답
        try (PrintWriter bufferedWriter = httpResponse.getWriter()
        ) {
            responseHeader = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.OK.getCode(),
                                                                StringUtils.DEFAULT_CHARSET,
                                                                responseBody.getBytes(StringUtils.DEFAULT_CHARSET).length);
            bufferedWriter.write(responseHeader);
            bufferedWriter.write(responseBody);
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        log.debug("userId {}",httpRequest.getAttribute("userId"));
        log.debug("userId : {}", httpRequest.getParameter("userId"));
        log.debug("userPassword : {}", httpRequest.getParameter("userPassword"));
        log.debug("userEmail : {}", httpRequest.getParameter("userEmail"));
    }
}
