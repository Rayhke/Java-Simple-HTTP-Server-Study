package com.nhnacademy.http;

import com.nhnacademy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@Slf4j
public class HttpRequestHandler implements Runnable {

    private static final int MAX_QUEUE_SIZE = 10;

    private final Queue<Socket> requestQueue;

    private final int maxQueueSize;

    public HttpRequestHandler() {
        this(MAX_QUEUE_SIZE);
    }

    public HttpRequestHandler(int maxQueueSize) {
        if (maxQueueSize < 1) {
            throw new IllegalArgumentException(
                    String.format("maxQueueSize is range Out! : %d", maxQueueSize)
            );
        }

        this.requestQueue = new LinkedList<>();
        this.maxQueueSize = maxQueueSize;
    }

    // =================================================================================================================
    // method

    public synchronized void addRequest(Socket client) {
        while (maxQueueSize <= requestQueue.size()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error("{}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }

        requestQueue.add(client);
        notify();
    }

    public synchronized Socket getRequest() {
        while (requestQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.error("{}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }

        notify();
        return requestQueue.poll();
    }

    // =================================================================================================================
    // thread

    @Override
    public void run() {
        while (true) {
            Socket client = getRequest();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {
                StringBuilder requestBuilder = new StringBuilder();
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
                close(client);
            }
        }
    }

    // =================================================================================================================

    private void close(Socket client) {
        try {
            if (Objects.nonNull(client)
                    && !client.isClosed()) client.close();
        } catch (IOException e) {
            log.error("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
