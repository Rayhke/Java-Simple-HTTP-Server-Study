package com.nhnacademy.http.util;

import java.util.Objects;

public final class StringUtils {

    // 운영체제에 따라 줄바꿈을 맞춰서 해줌
    public static final String CRLF = System.lineSeparator();

    public static final String DEFAULT_CHARSET = "UTF-8";

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
