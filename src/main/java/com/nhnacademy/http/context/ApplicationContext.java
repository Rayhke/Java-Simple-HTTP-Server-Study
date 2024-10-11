package com.nhnacademy.http.context;

import com.nhnacademy.http.context.exception.ContextParametersDeleteFail;
import com.nhnacademy.http.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ApplicationContext implements Context {

    private final ConcurrentMap<String, Object> objectMap;

    public ApplicationContext() {
        this.objectMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getAttribute(String name) {
        check(name);
        return objectMap.get(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        check(name);
        if (Objects.isNull(object)) {
            throw new IllegalArgumentException("object is Null!");
        }
        objectMap.put(name, object);
    }

    @Override
    public void removeAttribute(String name) {
        check(name);
        if (Objects.isNull(objectMap.remove(name))) {
            throw new ContextParametersDeleteFail();
        }
    }

    // =================================================================================================================

    private void check(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("name is Null!");
        }
    }
}
