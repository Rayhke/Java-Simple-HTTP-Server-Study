package com.nhnacademy.http.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StringUtils {

    // TODO : 표현이 잘못되었는 데, CR, LF 는 별개다.
    // 허나 순수 목적이, 운영체제에 따른 줄바꿈을 구현하는 것이 목적이기 때문에 이름을 이렇게 지었다.
    public static final String CRLF = System.lineSeparator();

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private StringUtils() {}

    // StringUtils.isEmpty() 과 비슷하다.
    public static boolean isNullOrEmpty(String s) {
        if (Objects.isNull(s)) {
            return true;
        }
        return s.replace(" ", "").isBlank();
    }

    // StringTypeData.trim() 는 앞뒤의 공백만 제거하고, 그 사이에 공백은 제거 하지 못한다.
    // StringTypeData.replace() 은 regex 에 지시한 문자열을 전부 replacement 으로 변환한다.
    // StringTypeData.replaceAll() 의 차이는 regex "[]" 안에 지시한 문자들은 전부 검증한다.
    // ex : regex "[ABC]" 면, (A, B, C) 인 문자열 인덱스 위치는 전부 replacement 로 치환
}
