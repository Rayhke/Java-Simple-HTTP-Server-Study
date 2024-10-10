package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;

import java.io.PrintWriter;

public class NotFoundHttpService implements HttpService {

    /* NotFoundHttpService 구현
        - 페이지를 찾을 수 없을 때 /resources/404.html 응답 합니다.
        - httpStatusCode : 404
        - Description: Not Found
     */
    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // Body - 설정
        String responseBody = ResponseUtils.tryGetBodyFromFile(ResponseUtils.DEFAULT_404);

        // Header - 설정
        String responseHeader = null;

        //PrintWriter 응답
        try(PrintWriter bufferedWriter = httpResponse.getWriter()
        ){
            responseHeader = ResponseUtils.createResponseHeader(ResponseUtils.HttpStatus.NOT_FOUND.getCode(),
                                                                httpResponse.getCharacterEncoding(),
                                                                responseBody.getBytes(httpResponse.getCharacterEncoding()).length);
            bufferedWriter.write(responseHeader);
            bufferedWriter.write(responseBody);
            bufferedWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
