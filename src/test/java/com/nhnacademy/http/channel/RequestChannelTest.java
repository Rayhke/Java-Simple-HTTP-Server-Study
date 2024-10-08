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

    @DisplayName("default queueSize : 10")
    @Test
    void constructorTest1() throws Exception {
        RequestChannel requestChannel = new RequestChannel();

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "queueSize", requestChannel);
        long queueSize = (long) readFieldValue.get();

        //TODO#113 기본 생성자를 이용해서 생성된 requestChannel의 queueSize가 10인지 검증 합니다.

    }

    @DisplayName("queueSize = -5")
    @Test
    void constructorTest2() {
        //TODO#114 RequestChannel 객체 생성시 queueSize -5 이면 IllegalArgumentException 발생하는지 검증 합니다.

    }

    @DisplayName("addRequest : 5 times")
    @Test
    void addRequest_5_times() throws Exception {
        RequestChannel requestChannel = new RequestChannel();
        //TODO#115 requestChannel에 5개의 아무것도 실행하지 않는 작업을(Executable) 등록 합니다. Executable : ()->{} 사용합니다.


        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        Assertions.assertEquals(5, queue.size());
    }

    @DisplayName("addRequest : 11 times, waiting")
    @Test
    void addRequest_11times() throws Exception {

        RequestChannel requestChannel = new RequestChannel(10);

        //TODO#116 requestChannel에 11개의 빈 작업을 등록하는 thread를 구현 하세요. 빈 작업: ()->{}
        Thread thread = null;

        thread.start();
        Thread.sleep(2000);

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        //requestChannel의 queueSize =10, 11번재 executable 객체를 추가할 수 없어 대기 함니다.
        log.debug("11-queueSize : {}", queue.size());
        Assertions.assertEquals(10, queue.size());
        thread.interrupt();
    }

    @DisplayName("getRequest, from queue(size:5)")
    @Test
    void getRequest() throws Exception {
        RequestChannel requestChannel = new RequestChannel(10);
        for (int i = 1; i <= 5; i++) {
            requestChannel.addHttpJob(() -> {
            });
        }
        //TODO#117 requestChannel 작업을 할당 받아 실행 하세요.
        Executable executable = null;

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(RequestChannel.class, "requestQueue", requestChannel);
        Queue queue = (Queue) readFieldValue.get();

        log.debug("queue-size : {}", queue.size());

        Assertions.assertEquals(4, queue.size());
    }

    @DisplayName("getRequest from empty queue")
    @Test
    void getRequest_from_empty_queue() throws InterruptedException {
        RequestChannel requestChannel = new RequestChannel(10);

        Thread thread = new Thread(() -> requestChannel.getHttpJob());
        thread.setName("my-thread");
        thread.start();

        Thread.sleep(2000);

        log.debug("{} : {}", thread.getName(), thread.getState());

        //TODO#118 thread의 상태가 WAITING 상태인지 검증 합니다.


        thread.interrupt();
    }
}