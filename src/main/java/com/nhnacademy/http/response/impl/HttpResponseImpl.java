package com.nhnacademy.http.response.impl;

import com.nhnacademy.http.response.HttpResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class HttpResponseImpl implements HttpResponse {

    private final Socket client;

    public HttpResponseImpl(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is null");
        }
        this.client = client;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(client.getOutputStream());
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }
}
