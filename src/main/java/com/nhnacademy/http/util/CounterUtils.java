package com.nhnacademy.http.util;

import com.nhnacademy.http.context.ContextHolder;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public final class CounterUtils {

    public static final String CONTEXT_COUNTER_NAME = "Global-Counter";

    private CounterUtils() {}

    public static synchronized long increaseAndGet() {
        return Stream.of(ContextHolder.getApplicationContext().getAttribute(CONTEXT_COUNTER_NAME))
                        .filter(AtomicLong.class::isInstance)
                        .map(o -> ((AtomicLong) o).incrementAndGet())
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new);
    }
}
