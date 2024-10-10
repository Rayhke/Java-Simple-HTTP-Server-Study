package com.nhnacademy.http.channel;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.request.impl.HttpRequestImpl;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.response.impl.HttpResponseImpl;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class HttpJob implements Executable {

    private final HttpRequest httpRequest;

    private final HttpResponse httpResponse;

    private final Socket client;

    public HttpJob(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is Null!");
        }
        this.client = client;
        this.httpRequest = new HttpRequestImpl(this.client);
        this.httpResponse = new HttpResponseImpl(this.client);
    }

    // =================================================================================================================
    // method

    public Socket getClient() {
        return client;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    @Override
    public void execute() {

        log.debug("method : {}", httpRequest.getMethod());
        log.debug("uri : {}", httpRequest.getRequestURI());
        log.debug("client-closed : {}", client.isClosed());

        boolean urlIsExist = ResponseUtils.isExist(httpRequest.getRequestURI());
        String responseBody = null;
        String responseHeader = null;

        try (BufferedWriter bufferedWriter = new BufferedWriter(httpResponse.getWriter())
        ) {
            responseBody = (urlIsExist) ?
                    ResponseUtils.tryGetBodyFromFile(httpRequest.getRequestURI())
                    : ResponseUtils.tryGetBodyFromFile(ResponseUtils.DEFAULT_404);
            responseHeader = (urlIsExist) ?
                    ResponseUtils.createResponseHeader(
                            ResponseUtils.HttpStatus.OK.getCode(),                              // TODO : 이 부분은 외부 enum을 쓰는 게 아닌, 실제로는 직접 코드를 기입해줘야 함.
                            httpResponse.getCharacterEncoding(),
                            responseBody.getBytes(httpResponse.getCharacterEncoding()).length)  // TODO : Charset 의 값을 전적으로 외부에 의존하기 때문에 세팅 주의
                    : ResponseUtils.createResponseHeader(
                            ResponseUtils.HttpStatus.NOT_FOUND.getCode(),                       // TODO : 이 부분은 외부 enum을 쓰는 게 아닌, 실제로는 직접 코드를 기입해줘야 함.
                            httpResponse.getCharacterEncoding(),
                            responseBody.getBytes(httpResponse.getCharacterEncoding()).length); // TODO : Charset 의 값을 전적으로 외부에 의존하기 때문에 세팅 주의

            bufferedWriter.write(responseHeader);
            bufferedWriter.write(responseBody);
            bufferedWriter.flush();
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            // TODO : 여기로 들어왔을 시점엔 이미 client 가 닫혀있음.
            close(); // 이 시점에서 HttpJob 에 있는 HttpRequest 와 HttpResponse 도 버림
        }
    }

    // =================================================================================================================

    private void close() {
        try {
            if (!client.isClosed()) {
                client.shutdownInput();
                client.shutdownOutput();
                log.debug("client -> Input : {}, Output : {}",
                        client.isInputShutdown(), client.isOutputShutdown());
                client.close();
            }
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
