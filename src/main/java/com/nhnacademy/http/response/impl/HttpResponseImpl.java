package com.nhnacademy.http.response.impl;

import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class HttpResponseImpl implements HttpResponse {

    private final DataOutputStream output;

    private Charset charset;

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
        this.charset = StringUtils.DEFAULT_CHARSET;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(output, false, getCharacterEncoding());
    }

    @Override
    public Charset getCharacterEncoding() {
        return charset;
    }

    // Charset.forName()
    @Override
    public void setCharacterEncoding(String charset) {
        if (StringUtils.isNullOrEmpty(charset)) {
            throw new IllegalArgumentException("charset is Null!");
        }
        // setCharacterEncoding(Charset.forName(charset));
        switch (charset.toUpperCase()) {
            case "US-ASCII":
                setCharacterEncoding(StandardCharsets.US_ASCII);
                break;
            case "ISO-8859-1":
                setCharacterEncoding(StandardCharsets.ISO_8859_1);
                break;
            case "UTF-8":
                setCharacterEncoding(StandardCharsets.UTF_8);
                break;
            case "UTF-16BE":
                setCharacterEncoding(StandardCharsets.UTF_16BE);
                break;
            case "UTF-16LE":
                setCharacterEncoding(StandardCharsets.UTF_16LE);
                break;
            case "UTF-16":
                setCharacterEncoding(StandardCharsets.UTF_16);
                break;
            default:
                log.error("Wrong charset value. : {}", charset);
                setCharacterEncoding(StringUtils.DEFAULT_CHARSET);
        }
    }

    @Override
    public void setCharacterEncoding(Charset charset) {
        if (Objects.isNull(charset)) {
            throw new IllegalArgumentException("charset is Null!");
        }
        this.charset = charset;
    }
}
