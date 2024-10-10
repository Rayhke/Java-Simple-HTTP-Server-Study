package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.ResponseUtils;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class InfoHttpService implements HttpService {

    /* InfoHttpService 구현
       - Request : http://localhost:8080/info.html?id=marco&age=40&name=마르코
       - 요청을 처리하고 응답하는 InfoHttpService 입니다.
       - IndexHttpService 를 참고하여 doGet 을 구현하세요.
       - info.html 파일은 /resources/info.html 위치 합니다.
       - info.html 을 읽어 parameters{id,name,age}를 replace 후 응답 합니다.
       - ex)
            ${id} <- marco
            ${name} <- 마르코
            ${age} <- 40
    */

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // Body - 설정
        String responseBody = ResponseUtils.tryGetBodyFromFile(httpRequest.getRequestURI());

        String id = httpRequest.getParameter("id");
        String name = httpRequest.getParameter("name");
        name = URLDecoder.decode(name, StandardCharsets.UTF_8);
        String age = httpRequest.getParameter("age");

        log.debug("id : {}", id);
        log.debug("name : {}", name);
        log.debug("age : {}", age);

        responseBody = responseBody.replace("${id}", id);
        responseBody = responseBody.replace("${name}", name);
        responseBody = responseBody.replace("${age}", age);

        // Header - 설정
        String responseHeader = null;

        // PrintWriter 를 이용한 응답
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
}
