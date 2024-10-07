package com.nhnacademy.http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SimpleHttpServer {

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private final ServerSocket serverSocket;

    public SimpleHttpServer() {
        this(DEFAULT_PORT);
    }

    public SimpleHttpServer(int port) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException(String.format("port is range Out! : %d", port));
        }
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =================================================================================================================

    public synchronized void start() throws IOException {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket client = serverSocket.accept();
                Thread thread = new Thread(new HttpRequestHandler(client));
                thread.start();
            }
        } catch (Exception e) {
            log.debug("{}", e.getMessage(), e);
        }
    }
}
