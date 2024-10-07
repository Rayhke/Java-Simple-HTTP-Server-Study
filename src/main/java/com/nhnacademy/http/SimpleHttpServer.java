package com.nhnacademy.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SimpleHttpServer {

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private final AtomicLong atomicCounter;

    public SimpleHttpServer() {
        this(DEFAULT_PORT);
    }

    public SimpleHttpServer(int port) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException(String.format("port is range Out! : %d", port));
        }

        this.port = port;
        this.atomicCounter = new AtomicLong();
        /*try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    // =================================================================================================================

    public synchronized void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)
        ) {
            HttpRequestHandler httpRequestHandlerA = new HttpRequestHandler();
            HttpRequestHandler httpRequestHandlerB = new HttpRequestHandler();

            Thread threadA = new Thread(httpRequestHandlerA);
            threadA.setName("threadA");
            threadA.start();

            Thread threadB = new Thread(httpRequestHandlerB);
            threadB.setName("threadB");
            threadB.start();

            while (true) {
                Socket client = serverSocket.accept();
                if ((atomicCounter.getAndDecrement() & 1) == 0) {
                    httpRequestHandlerA.addRequest(client);
                } else {
                    httpRequestHandlerB.addRequest(client);
                }
            }
        } catch (Exception e) {
            log.debug("{}", e.getMessage(), e);
        }
    }
}
