package com.nhnacademy.http;

import com.nhnacademy.http.channel.Executable;
import com.nhnacademy.http.channel.RequestChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HttpRequestHandlerTest {

    @DisplayName("RequestChannel = null")
    @Test
    void constructorTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new HttpRequestHandler(null));
    }

    @DisplayName("producer & consumer")
    @Test
    void run() {
        RequestChannel requestChannel = new RequestChannel();
        HttpRequestHandler requestHandler = new HttpRequestHandler(requestChannel);

        AtomicInteger counter = new AtomicInteger();

        // Executable countExecutable = counter::incrementAndGet;
        Executable countExecutable = () -> {
            counter.incrementAndGet();
            log.debug("count : {}", counter.get());
        };
        /*Executable countExecutable = new Executable() {
            @Override
            public void execute() {
                counter.incrementAndGet();
                log.debug("count : {}", counter.get());
            }
        };*/

        Thread producer = new Thread(() -> {
            for (int n = 0; n < 5;n++) {
                try {
                    Thread.sleep(1000);
                    requestChannel.addHttpJob(countExecutable);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        producer.start();

        Thread consumer = new Thread(requestHandler);
        consumer.start();

        do {
            Thread.yield();
        } while (producer.isAlive());

        log.debug("counter : {}", counter.get());

        Assertions.assertEquals(5, counter.get());
    }
}