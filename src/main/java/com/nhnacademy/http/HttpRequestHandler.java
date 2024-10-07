package com.nhnacademy.http;

import com.nhnacademy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

@Slf4j
public class HttpRequestHandler implements Runnable {

    private final Socket client;

    public HttpRequestHandler(Socket client) {
        if (Objects.isNull(client) || client.isClosed()) {
            throw new IllegalArgumentException("client is Null!");
        }
        this.client = client;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
        ) {
            StringBuilder requestBuilder = new StringBuilder();
            log.debug("------HTTP-REQUEST_start()");
            while (true) {
                String line = bufferedReader.readLine();
                requestBuilder.append(line);
                log.debug("{}", line);

                if (StringUtils.isNullOrEmpty(line)) { break; }
            }
            log.debug("------HTTP-REQUEST_end()");

            StringBuilder responseBody = new StringBuilder();
            responseBody.append("<html>");
            responseBody.append("<body>");
            responseBody.append("<h1>Hello Java</h1>");
            responseBody.append("</body>");
            responseBody.append("</html>");

            StringBuilder responseHeader = new StringBuilder();
            responseHeader.append(String.format("HTTP/1.0 200 OK%s", StringUtils.CRLF));
            responseHeader.append(String.format("Server: HTTP server/0.1%s", StringUtils.CRLF));
            responseHeader.append(String.format("Content-type: text/html; charset=%s%s", "UTF-8", StringUtils.CRLF));
            responseHeader.append(String.format("Connection: Closed%s", StringUtils.CRLF));
            responseHeader.append(String.format("Content-Length:%d %s%s", responseBody.toString().getBytes().length, StringUtils.CRLF, StringUtils.CRLF));

            bufferedWriter.write(responseHeader.toString());
            bufferedWriter.write(responseBody.toString());
            bufferedWriter.flush();

            log.debug("header : {}", responseHeader);
            log.debug("body : {}", responseBody);

        } catch (IOException e) {
            log.error("socket error : {}", e.getMessage(), e);
        } finally {
            close();
        }
    }

    // =================================================================================================================

    private void close() {
        try {
            if (Objects.nonNull(client)
                    && !client.isClosed()) client.close();
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
