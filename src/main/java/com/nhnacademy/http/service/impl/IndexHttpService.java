package com.nhnacademy.http.service.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.util.CounterUtils;
import com.nhnacademy.http.util.ResponseUtils;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;

/**
 * <a href="http://localhost:8080/index.html">테스트</a>
 */
@Slf4j
public class IndexHttpService implements HttpService {

    public static final String URL = "/index.html";

    @Override
    public void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        // Body - 설정
        String responseBody = ResponseUtils.tryGetBodyFromFile(httpRequest.getRequestURI());

        long count = CounterUtils.increaseAndGet();
        String userId = httpRequest.getParameter("userId");

        log.debug("count : {}", count);
        log.debug("userId : {}", userId);

        responseBody = responseBody.replace("${count}", String.valueOf(count));

        responseBody = (!StringUtils.isNullOrEmpty(userId)) ?
                        responseBody.replace("${userId}", userId)
                        : responseBody.replace("<h2>${userId}님의 회원가입이 완료 되었습니다!</h2>", "");

        // Header - 설정
        String responseHeader = null;

        // PrintWriter 응답
        try (PrintWriter bufferedWriter = httpResponse.getWriter()
        ) {
            // TODO : 저기서 contentLength 는 문자 타입에 따라 문자 하나의 값이 천차 만별이다.
            // TODO : 추후 httpResponse.getCharacterEncoding() 이 정상 동작하는 지 체크,
            // 문자 하나 당, 1 ~ 4 byte 갈린다. (한국어는 2 byte)
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
