package com.nhnacademy.http.channel;

import com.nhnacademy.http.context.Context;
import com.nhnacademy.http.context.ContextHolder;
import com.nhnacademy.http.context.exception.ObjectNotFoundException;
import com.nhnacademy.http.service.exception.MethodNotAllowed;
import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.request.impl.HttpRequestImpl;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.response.impl.HttpResponseImpl;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.service.impl.MethodNotAllowedService;
import com.nhnacademy.http.service.impl.NotFoundHttpService;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.stream.Stream;

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
        if (urlIsExist) {
            try {
                Context context = ContextHolder.getApplicationContext();
                Stream.of(context.getAttribute(httpRequest.getRequestURI()))
                                    .filter(HttpService.class::isInstance)
                                    .map(o -> (HttpService) o)
                                    .findFirst()
                                    .get()
                                    .service(getHttpRequest(), getHttpResponse());
                close();
            } catch (ObjectNotFoundException e) {
                new NotFoundHttpService()
                        .service(getHttpRequest(), getHttpResponse());
            } catch (MethodNotAllowed e) {
                new MethodNotAllowedService()
                        .service(getHttpRequest(), getHttpResponse());
                log.error("{}", e.getCause(), e);
            }
        } else {
            // TODO : 위의 부분이 중복되는 느낌이 있다.
            new NotFoundHttpService()
                    .service(getHttpRequest(), getHttpResponse());
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
