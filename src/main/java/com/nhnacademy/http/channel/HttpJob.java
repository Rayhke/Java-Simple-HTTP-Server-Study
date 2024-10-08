package com.nhnacademy.http.channel;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.request.impl.HttpRequestImpl;
import com.nhnacademy.http.response.HttpResponse;
import com.nhnacademy.http.response.impl.HttpResponseImpl;
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
public class HttpJob implements Executable {

    private final HttpRequest httpRequest;

    private final HttpResponse httpResponse;

    private final Socket client;

    public HttpJob(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is null");
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

    @Override
    public void execute() {

        // HttpJob 는 execute() method 를 구현 합니다. step2~3 참고하여 구현합니다.
        // <html><body><h1>thread-0:hello java</h1></body>
        // <html><body><h1>thread-1:hello java</h1></body>
        // <html><body><h1>thread-2:hello java</h1></body>
        // ...

        log.debug("method : {}", httpRequest.getMethod());
        log.debug("uri : {}", httpRequest.getRequestURI());
        log.debug("client-closed : {}", client.isClosed());

        /*StringBuilder requestBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
        ) {
            log.debug("------HTTP-REQUEST_start()");
            while (true) {
                String line = bufferedReader.readLine();
                requestBuilder.append(line);
                log.debug("{}", line);

                if (StringUtils.isNullOrEmpty(line)) {
                    break;
                }
            }
            log.debug("------HTTP-REQUEST_end()");


            StringBuilder responseBody = new StringBuilder();
            responseBody.append("<html>");
            responseBody.append("<body>");
            responseBody.append(String.format("<h1>[%s] : Hello Java</h1>", Thread.currentThread().getName()));
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
        }*/
    }

    // =================================================================================================================

    private void close() {
        try {
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
