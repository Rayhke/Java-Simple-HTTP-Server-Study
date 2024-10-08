package com.nhnacademy.http.channel;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RequestChannelTest {

    private static final long DEFAULT_QUEUE_SIZE = 10L;

    @DisplayName("default queueSize : 10")
    @Test
    void constructorTest1() throws Exception {
        RequestChannel requestChannel = new RequestChannel();

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "queueSize", requestChannel);
        long queueSize = (long) readFieldValue.get();

        log.debug("default queueSize : {}", queueSize);
        Assertions.assertEquals(DEFAULT_QUEUE_SIZE, queueSize);
    }

    @DisplayName("queueSize = -5")
    @Test
    void constructorTest2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RequestChannel(-5));
    }

    @DisplayName("addRequest : 5 times")
    @Test
    void addRequest_5_times() throws Exception {
        RequestChannel requestChannel = new RequestChannel();
        // requestChannel 에 5개의 아무것도 실행하지 않는 작업을 (Executable) 등록 합니다. Executable : () -> {} 사용합니다.
        for (int n = 0; n < 5; n++) {
            requestChannel.addHttpJob(() -> {});
        }

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        Assertions.assertEquals(5, queue.size());
    }

    @DisplayName("addRequest : 11 times, waiting")
    @Test
    void addRequest_11times() throws Exception {

        RequestChannel requestChannel = new RequestChannel(10);

        // requestChannel 에 11개의 빈 작업을 등록하는 thread 를 구현 하세요. 빈 작업 : () -> {}
        Thread thread = new Thread(() -> {
            for (int n = 0; n < 11; n++) {
                requestChannel.addHttpJob(() -> {});
            }
        });
        thread.start();
        Thread.sleep(2000);

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        //requestChannel 의 queueSize = 10, 11번재 executable 객체를 추가할 수 없어 대기 함니다.
        log.debug("11-queueSize : {}", queue.size());
        Assertions.assertEquals(10, queue.size());

        thread.interrupt();
    }

    @DisplayName("getRequest, from queue(size:5)")
    @Test
    void getRequest() throws Exception {
        RequestChannel requestChannel = new RequestChannel(10);
        for (int i = 0; i < 5; i++) {
            requestChannel.addHttpJob(() -> {});
        }

        Executable executable = requestChannel.getHttpJob();
        executable.execute();

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        log.debug("queue-size : {}", queue.size());

        Assertions.assertEquals(4, queue.size());
    }

    @DisplayName("getRequest from empty queue")
    @Test
    void getRequest_from_empty_queue() throws InterruptedException {
        RequestChannel requestChannel = new RequestChannel(10);

        Thread thread = new Thread(requestChannel::getHttpJob);
        // Thread thread = new Thread(() -> requestChannel.getHttpJob());
        thread.setName("my-thread");
        thread.start();

        Thread.sleep(2000);

        log.debug("{} : {}", thread.getName(), thread.getState());
        Assertions.assertEquals(Thread.State.WAITING, thread.getState());

        thread.interrupt();
    }
}