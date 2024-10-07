package com.nhnacademy.http;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

import java.net.Socket;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HttpRequestHandlerQueueTest {

    private static final int MAX_QUEUE_SIZE = 10;

    private HttpRequestHandler httpRequestHandler;

    static class TestSocket extends Socket {
        private final String name;

        public TestSocket(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @BeforeEach
    void setUp() {
        httpRequestHandler = new HttpRequestHandler();
        for (int i = 0; i < 9; i++) {
            httpRequestHandler.addRequest(new TestSocket(String.format("socket%d", i)));
        }
    }

    @DisplayName("addRequest, queueSize : 9 -> 10")
    @Test
    void addRequest() throws Exception {
        // Socket10 10번째 client 를 추가 합니다.
        httpRequestHandler.addRequest(new TestSocket("socket10"));
        // Queue<Socket> requestQueue

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(HttpRequestHandler.class, "requestQueue", httpRequestHandler);
        Queue<Socket> requestQueue = (Queue<Socket>) readFieldValue.get();

        log.debug("requestQueue-size : {}", requestQueue.size());
        Assertions.assertEquals(MAX_QUEUE_SIZE, requestQueue.size());
    }

    @DisplayName("getRequest : socket0")
    @Test
    void getRequest() {
        // httpRequestHandler.getRequest(); 호출 했을 때 socket0 반환되는지 검증 합니다.
        TestSocket actual = (TestSocket) httpRequestHandler.getRequest();
        Assertions.assertEquals("socket0", actual.getName());
    }

    @DisplayName("blocking queue test : queue size : 10, 11번째 Socket 를 추가 한다면, consumer 에 의해서 소비될 때 까지 대기 합니다.")
    @Test
    void blockingTest() throws Exception {

        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                TestSocket testSocket9 = new TestSocket("socket9");
                TestSocket testSocket10 = new TestSocket("socket10");

                httpRequestHandler.addRequest(testSocket9);
                log.debug("2초 대기 후 socket10 추가 됨");
                httpRequestHandler.addRequest(testSocket10);
            }
        });
        producer.start();

        // 2초 대기후 enteringQueue.getCustomer() 호출해서 소비할 수 있도록 consumer Thread 를 구현 합니다.
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //2초 대기 후 getCustomer()를 호출 합니다.
                    Thread.sleep(2000);
                    TestSocket testSocket = (TestSocket) httpRequestHandler.getRequest();
                    log.debug("getRequest : {}", testSocket.getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        consumer.start();

        do {
            Thread.yield();
        } while (producer.isAlive() || consumer.isAlive());

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(HttpRequestHandler.class, "requestQueue", httpRequestHandler);
        Queue<Socket> requestQueue = (Queue<Socket>) readFieldValue.get();

        // requestQueue.size() 가 10인지 검증 합니다.
        log.debug("requestQueue-size : {}", requestQueue.size());
        Assertions.assertEquals(MAX_QUEUE_SIZE, requestQueue.size());
    }
}
