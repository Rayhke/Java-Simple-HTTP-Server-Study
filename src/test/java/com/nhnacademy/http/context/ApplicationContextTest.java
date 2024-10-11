package com.nhnacademy.http.context;

import com.nhnacademy.http.context.exception.ObjectNotFoundException;
import com.nhnacademy.http.service.impl.IndexHttpService;
import com.nhnacademy.http.service.impl.InfoHttpService;
import com.nhnacademy.http.util.CounterUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationContextTest {

    @BeforeEach
    void setUp() {
        Context context = ContextHolder.getApplicationContext();

        // name : indexHttpService, object : new IndexHttpService()
        context.setAttribute("indexHttpService", new IndexHttpService());
    }

    @Order(1)
    @Test
    void setAttribute1() {
        Context context = ContextHolder.getApplicationContext();
        context.setAttribute("name", new Object());

        Assertions.assertTrue(Objects.nonNull(context.getAttribute("name")));
    }

    @DisplayName("setAttribute object is null")
    @Order(2)
    @Test
    void setAttribute2() {
        Context context = ContextHolder.getApplicationContext();

        // context 에 다음과 같이 null Object 를 동록시 IllegalArgumentException 이 발생하는지 검증 합니다.
        // - context.setAttribute("something", null);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> context.setAttribute("something", null));
    }

    @Order(3)
    @Test
    void removeAttribute1() {
        Context context = ContextHolder.getApplicationContext();
        String name = "indexHttpService";

        context.removeAttribute(name);

        // name 에 해당되는 객체를 remove 했습니다. name 에 해당되는 객체를 다음과 같이 context 로 부터 획득하려고 할 때 ObjectNotFoundException 이 발생하는지 검증 합니다.
        // - context.getAttribute(name);
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> context.getAttribute(name));
    }

    @DisplayName("removeAttribute name is {null or empty}")
    @Order(4)
    @Test
    void removeAttribute2() {
        Context context = ContextHolder.getApplicationContext();
        String name1 = null;
        String name2 = "";
        String name3 = "   ";

        // context.removeAttribute(""); or context.removeAttribute(null); 실행할 때  IllegalArgumentException 발생하는지 검증 합니다.
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.removeAttribute(name1)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.removeAttribute(name2)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.removeAttribute(name3))
        );
    }

    @Order(5)
    @Test
    void getAttribute1() {
        Context context = ContextHolder.getApplicationContext();
        InfoHttpService infoHttpService = new InfoHttpService();
        context.setAttribute("infoHttpService", infoHttpService);
        Assertions.assertEquals(infoHttpService, context.getAttribute("infoHttpService"));
    }

    @DisplayName("getAttribute, object not found exception")
    @Order(6)
    @Test
    void getAttribute2() {
        Context context = ContextHolder.getApplicationContext();
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> context.getAttribute("something"));
    }

    @DisplayName("getAttribute name is null or empty")
    @Order(7)
    @Test
    void getAttribute3() {
        Context context = ContextHolder.getApplicationContext();
        String name1 = null;
        String name2 = "";
        String name3 = "   ";

        // getAttribute 를 다음과 같이 호출할 때 IllegalArgumentException Exception 이 발생하는지 검증하세요
        // - context.getAttribute(null);
        // - context.getAttribute("");
        Assertions.assertAll(
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.getAttribute(name1)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.getAttribute(name2)),
                () -> Assertions.assertThrows(IllegalArgumentException.class, () -> context.getAttribute(name3))
        );
    }

    @DisplayName("shared ContextHolder")
    @Order(8)
    @Test
    void sharedContextHolder() throws InterruptedException {
        long expected = 10L;

        Thread thread1 = new Thread(() -> {
            // thread 내에서 context 에 counter 값을 10으로 설정 합니다.
            Context context = ContextHolder.getApplicationContext();
            context.setAttribute(CounterUtils.CONTEXT_COUNTER_NAME, new AtomicLong(expected));
        });

        thread1.start();
        thread1.join();

        Thread thread2 = new Thread(() -> {
            // thread 내에서 context 에 counter = counter + 1 후, context 에 재 할당 합니다.
            // 내가 만든 구조는 AtomicLong 객체를 할당 해놨기 때문에,
            // AtomicLong 객체를 직접적으로 혹은, 아래의 구조와 동일한 CounterUtils.increaseAndGet() 와 상호작용 하면 된다.
            Context context = ContextHolder.getApplicationContext();
            Stream.of(context.getAttribute(CounterUtils.CONTEXT_COUNTER_NAME))
                    .filter(AtomicLong.class::isInstance)
                    .map(AtomicLong.class::cast)
                    .findFirst()
                    .map(AtomicLong::incrementAndGet);
        });

        thread2.start();
        thread2.join();

        Context context = ContextHolder.getApplicationContext();
        AtomicLong actual = (AtomicLong) context.getAttribute(CounterUtils.CONTEXT_COUNTER_NAME);
        log.debug("counter : {}", actual.get());
        Assertions.assertEquals(11L, actual.get());
    }
}