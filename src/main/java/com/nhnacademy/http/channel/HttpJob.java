package com.nhnacademy.http.channel;

import com.nhnacademy.http.error.MethodNotAllowed;
import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.request.impl.HttpRequestImpl;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.response.impl.HttpResponseImpl;
import com.nhnacademy.http.service.HttpService;
import com.nhnacademy.http.service.impl.IndexHttpService;
import com.nhnacademy.http.service.impl.InfoHttpService;
import com.nhnacademy.http.service.impl.MethodNotAllowedService;
import com.nhnacademy.http.service.impl.NotFoundHttpService;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

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

        HttpService httpService = null;
        boolean urlIsExist = ResponseUtils.isExist(httpRequest.getRequestURI());
        if (urlIsExist) {
            try {
                switch (httpRequest.getRequestURI()) {
                    case "/index.html":
                        httpService = new IndexHttpService(); break;
                    case "/info.html":
                        httpService = new InfoHttpService(); break;
                    default:
                        httpService = new NotFoundHttpService();
                }
                httpService.service(getHttpRequest(), getHttpResponse());
            } catch (MethodNotAllowed e) {
                httpService = new MethodNotAllowedService();
                httpService.service(getHttpRequest(), getHttpResponse());
                log.error("{}", e.getCause(), e);
            }
        } else {
            httpService = new NotFoundHttpService();
            httpService.service(getHttpRequest(), getHttpResponse());
        }
        close();
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
