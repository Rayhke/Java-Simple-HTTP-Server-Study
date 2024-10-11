package com.nhnacademy.http.context;

// Application 이 구동되는 환경을 Context 라고 합니다.
public interface Context {

    // Object 를 얻습니다.
    Object getAttribute(String name);

    // Object 를 등록합니다.
    void setAttribute(String name, Object object);

    // Object 를 삭제합니다.
    void removeAttribute(String name);
}
