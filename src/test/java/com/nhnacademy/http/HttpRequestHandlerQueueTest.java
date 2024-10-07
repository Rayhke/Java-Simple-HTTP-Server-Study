package com.nhnacademy.http;

import lombok.extern.slf4j.Slf4j;
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

    @Test
    @DisplayName("addRequest, queueSize : 9 -> 10")
    void addRequest() throws Exception {
        //Socket10 10번째 cleint를 추가 합니다.
        httpRequestHandler.addRequest(new TestSocket("socket10"));
        //Queue<Socket> requestQueue

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(HttpRequestHandler.class, "requestQueue", httpRequestHandler);
        Queue<Socket> requestQueue = (Queue<Socket>) readFieldValue.get();

        log.debug("requestQueue-size:{}", requestQueue.size());
        //TODO#101 - requestQueue.size() 10인지 검증 합니다.


    }

    @Test
    @DisplayName("getRequest : socket0")
    void getRequest() {
        //TODO#102 httpRequestHandler.getRequest(); 호출 했을 때 socket0 반환되는지 검증 합니다.

    }

    @Test
    @DisplayName("blocking queue test : queue size : 10, 11번째 Socket를 추가 한다면, consumer에 의해서 소비될 때 까지 대기 합니다.")
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

        // 2초 대기후 enteringQueue.getCustomer() 호출해서 소비할 수 있도록 consumer Thread를 구현 합니다.
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //2초 대기 후 getCustomer()를 호출 합니다.
                    Thread.sleep(2000);
                    TestSocket testSocket = (TestSocket) httpRequestHandler.getRequest();
                    log.debug("getRequest:{}", testSocket.getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        consumer.start();

        //TODO#103 producer or consumer thread가 실행 중 이라면 대기 합니다. yield()를 이용해서 구현 하세요.


        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(HttpRequestHandler.class, "requestQueue", httpRequestHandler);
        Queue<Socket> requestQueue = (Queue<Socket>) readFieldValue.get();

        //TODO#104 requestQueue.size()가 10인지 검증 합니다.

    }
}
