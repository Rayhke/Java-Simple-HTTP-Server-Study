package com.nhnacademy.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SimpleHttpServerConstructorTest {

    @DisplayName("port:-8080")
    @Test
    void constructorPortCheck() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SimpleHttpServer(-8080));
    }
}
