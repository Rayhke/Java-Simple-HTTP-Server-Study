package com.nhnacademy.http.request.impl;

import com.nhnacademy.http.request.HttpRequest;

import java.net.Socket;
import java.util.Map;
import java.util.Objects;

public class HttpRequestImpl implements HttpRequest {

    private final Socket client;

    public HttpRequestImpl(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is null");
        }
        this.client = client;
    }

    // GET /index.html?id=marco&age=40&name=마르코 HTTP/1.1
    @Override
    public String getMethod() {
        return "";
    }

    @Override
    public String getParameter(String name) {
        return "";
    }

    @Override
    public Map<String, String> getParameterMap() {

        return Map.of();
    }

    @Override
    public String getHeader(String name) {
        return "";
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public String getRequestURI() {
        return "";
    }
}
