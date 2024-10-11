package com.nhnacademy.http;

import com.nhnacademy.http.channel.HttpJob;
import com.nhnacademy.http.channel.RequestChannel;
import com.nhnacademy.http.context.Context;
import com.nhnacademy.http.context.ContextHolder;
import com.nhnacademy.http.service.impl.IndexHttpService;
import com.nhnacademy.http.service.impl.InfoHttpService;
import com.nhnacademy.http.service.impl.MethodNotAllowedService;
import com.nhnacademy.http.service.impl.NotFoundHttpService;
import com.nhnacademy.http.service.impl.RegisterHttpService;
import com.nhnacademy.http.util.CounterUtils;
import com.nhnacademy.http.util.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SimpleHttpServer {

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private final RequestChannel requestChannel;

    private WorkerThreadPool workerThreadPool;

    public SimpleHttpServer() {
        this(DEFAULT_PORT);
    }

    public SimpleHttpServer(int port) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException(String.format("port is range Out! : %d", port));
        }
        this.port = port;
        this.requestChannel = new RequestChannel();
        this.workerThreadPool = new WorkerThreadPool(requestChannel);

        Context context = ContextHolder.getApplicationContext();
        context.setAttribute(IndexHttpService.URL, new IndexHttpService());
        context.setAttribute(InfoHttpService.URL, new InfoHttpService());
        context.setAttribute(RegisterHttpService.URL, new RegisterHttpService());
        context.setAttribute(ResponseUtils.DEFAULT_404, new NotFoundHttpService());
        context.setAttribute(ResponseUtils.DEFAULT_405, new MethodNotAllowedService());
        context.setAttribute(CounterUtils.CONTEXT_COUNTER_NAME, new AtomicLong());
    }

    // =================================================================================================================
    // method

    public synchronized void start() throws IOException {
        workerThreadPool.start();

        try (ServerSocket serverSocket = new ServerSocket(port)
        ) {
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                requestChannel.addHttpJob(new HttpJob(client));
            }
        } catch (Exception e) {
            log.debug("server error : {}", e.getMessage(), e);
        }
    }
}
