package com.nhnacademy.http.response.impl;

import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Objects;

@Slf4j
public class HttpResponseImpl implements HttpResponse {

    private final DataOutputStream output;

    private String charset = "UTF-8";

    private final Socket client;

    public HttpResponseImpl(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is null");
        }
        this.client = client;
        try {
            this.output = new DataOutputStream(this.client.getOutputStream());
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output, false, Charset.forName(getCharacterEncoding()));
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        if (StringUtils.isNullOrEmpty(charset)) {
            throw new IllegalArgumentException("charset is Null!");
        }
        this.charset = charset;
    }
}
