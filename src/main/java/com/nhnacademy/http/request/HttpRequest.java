package com.nhnacademy.http.request;

import java.util.Map;

public interface HttpRequest {

    // request 에 설정된 값을 반환합니다.
    Object getAttribute(String name);

    // request 에 값을 (name->value) 설정 합니다., view(html)에 값을 전달 하기 위해서 사용 합니다.
    void setAttribute(String name, Object o);

    // parameter 를 map 형태로 반환 합니다.
    Map<String, String> getParameterMap();

    // ?page=1&sort=age, ex) getParameter("sort") , return age
    String getParameter(String name);

    // 개인이 임의로 추가
    String getHost();

    // GET, POST, ....
    String getMethod();

    // 요청 경로를 반환 합니다. GET /index.html?page=1 -> /index.html
    String getRequestURI();

    // header 의 value 를 반환 합니다.
    String getHeader(String name);
}
