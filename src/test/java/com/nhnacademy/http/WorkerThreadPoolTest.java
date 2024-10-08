package com.nhnacademy.http;

import com.nhnacademy.http.channel.RequestChannel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.function.Try;
import org.junit.platform.commons.util.ReflectionUtils;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorkerThreadPoolTest {

    private static final int DEFAULT_POOL_SIZE = 10;

    private static RequestChannel requestChannel;

    private static WorkerThreadPool threadPool;

    @BeforeAll
    static void beforeAllSetUp() {
        requestChannel = new RequestChannel();
        threadPool = new WorkerThreadPool(DEFAULT_POOL_SIZE, requestChannel);
        threadPool.start();
    }

    @DisplayName("poolSize < 0")
    @Order(1)
    @Test
    void constructorTest1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new WorkerThreadPool(-1, requestChannel));
    }

    @DisplayName("runnable parameter check")
    @Order(2)
    @Test
    void constructorTest2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new WorkerThreadPool(null));
    }

    @DisplayName("thread-pool size : 10")
    @Order(3)
    @Test
    void constructorTest3() throws Exception {
        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(WorkerThreadPool.class, "workerThreads", threadPool);
        Thread[] workerThreads = (Thread[]) readFieldValue.get();

        log.debug("thread-pool size : {}", workerThreads.length);
        Assertions.assertEquals(DEFAULT_POOL_SIZE, workerThreads.length);
    }

    @DisplayName("thread start, thread Status check : alive")
    @Order(4)
    @Test
    void start() throws Exception {
        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(WorkerThreadPool.class, "workerThreads", threadPool);
        Thread[] workerThreads = (Thread[]) readFieldValue.get();

        int aliveCount = 0;
        for (Thread thread : workerThreads) {
            if (thread.isAlive()) { aliveCount++; }
        }

        log.debug("aliveCount : {}", aliveCount);
        Assertions.assertEquals(DEFAULT_POOL_SIZE, aliveCount);
    }

    @DisplayName("thread stop, thread Status : TERMINATED")
    @Order(5)
    @Test
    void stop() throws Exception {
        threadPool.stop();

        Try<Object> readFieldValue = ReflectionUtils.tryToReadFieldValue(WorkerThreadPool.class, "workerThreads", threadPool);
        Thread[] workerThreads = (Thread[]) readFieldValue.get();

        int terminatedCount = 0;
        for (Thread thread : workerThreads) {
            if (thread.getState().equals(Thread.State.TERMINATED)) { terminatedCount++; }
        }

        log.debug("terminatedCount : {}", terminatedCount);
        Assertions.assertEquals(10, terminatedCount);
    }
}