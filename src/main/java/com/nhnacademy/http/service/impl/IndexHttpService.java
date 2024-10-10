package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

@Slf4j
public class IndexHttpService implements HttpService {

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {

        // Body - 설정
        String responseBody = null;

        // Header - 설정
        String responseHeader = null;

        // PrintWriter 응답
        try (PrintWriter bufferedWriter = httpResponse.getWriter()
        ) {
            responseBody = ResponseUtils.tryGetBodyFromFile(httpRequest.getRequestURI());
            responseHeader = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.OK.getCode(),
                                                                httpResponse.getCharacterEncoding(),
                                                                responseBody.getBytes(httpResponse.getCharacterEncoding()).length);
            bufferedWriter.write(responseHeader);
            bufferedWriter.write(responseBody);
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
