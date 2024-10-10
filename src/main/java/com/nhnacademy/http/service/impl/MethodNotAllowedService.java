package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import com.nhnacademy.http.util.StringUtils;

import java.io.PrintWriter;

public class MethodNotAllowedService implements HttpService {

    /* MethodNotAllowedService 구현
        - index.html -> doGet() 구현되어 있습니다. -> POST 요청을 하면 405 method not allowed 응답 합니다.
        - httpStatusCode : 405
        - Description: Method Not Allowed
        - /resources/405.html 응답 합니다.
     */

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        // Body - 설정
        String responseBody = ResponseUtils.tryGetBodyFromFile(ResponseUtils.DEFAULT_405);

        // Header - 설정
        String responseHeader = null;

        // PrintWriter 응답
        try (PrintWriter bufferedWriter = httpResponse.getWriter();
        ) {
            responseHeader = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.METHOD_NOT_FOUND.getCode(),
                                                                StringUtils.DEFAULT_CHARSET,
                                                                responseBody.getBytes(StringUtils.DEFAULT_CHARSET).length);
            bufferedWriter.write(responseHeader);
            bufferedWriter.write(responseBody);
            bufferedWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
