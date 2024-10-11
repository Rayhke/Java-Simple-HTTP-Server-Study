package com.nhnacademy.http.util;

import com.nhnacademy.http.context.ContextHolder;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public final class CounterUtils {

    public static final String CONTEXT_COUNTER_NAME = "Global-Counter";

    private CounterUtils() {}

    // AtomicLong.class::cast 는 타입 캐스팅이다.
    // TODO : 의도대로 동작하는 지, 추후 검증할 것
    public static synchronized long increaseAndGet() {
        return Stream.of(ContextHolder.getApplicationContext()
                                        .getAttribute(CONTEXT_COUNTER_NAME))
                        .filter(AtomicLong.class::isInstance)
                        .map(AtomicLong.class::cast)
                        .findFirst()
                        .map(AtomicLong::incrementAndGet)
                        .orElseThrow(IllegalAccessError::new);
    }
}
