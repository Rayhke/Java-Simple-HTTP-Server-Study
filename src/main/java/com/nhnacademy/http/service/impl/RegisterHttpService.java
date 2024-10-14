package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import com.nhnacademy.http.util.ResponseUtils.HttpStatus;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;
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

    // TODO : '@' 문자가 '%40' 으로 변환되는 데, 이것을 원래대로 돌릴 방법이 뭘까?
    @Override
    public void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {

        String key = "userId";

        log.debug("userId : {}", httpRequest.getParameter(key));
        log.debug("userPassword : {}", httpRequest.getParameter("userPassword"));
        log.debug("userEmail : {}", httpRequest.getParameter("userEmail"));

        HttpStatus httpStatus = HttpStatus.REDIRECT;

        // TODO : HTTP 301 API 문서 정독하기
        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append(String.format("HTTP/1.1 %d %s%s", httpStatus.getCode(), httpStatus.getDescription(), StringUtils.CRLF));
        responseHeader.append(String.format("Location: http://%s/%s?%s=%s",
                httpRequest.getHost(), "index.html", key, httpRequest.getParameter(key)));

        try (PrintWriter bufferedWriter = httpResponse.getWriter()
        ) {
            bufferedWriter.write(responseHeader.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
