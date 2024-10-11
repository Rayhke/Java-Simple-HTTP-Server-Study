package com.nhnacademy.http.context;

import com.nhnacademy.http.context.exception.ContextParametersDeleteFail;
import com.nhnacademy.http.context.exception.ObjectNotFoundException;
import com.nhnacademy.http.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// Context 에는 객체를 생성 후, (등록 / 삭제) 할 수 있습니다.
// 즉 공유할 수 있는 환경 입니다.
public class ApplicationContext implements Context {

    private final ConcurrentMap<String, Object> objectMap;

    public ApplicationContext() {
        this.objectMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getAttribute(String name) {
        check(name);
        return Optional.ofNullable(objectMap.get(name))
                .orElseThrow(() -> new ObjectNotFoundException(name));
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
