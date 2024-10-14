package com.nhnacademy.http.request.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.http.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class HttpRequestImpl implements HttpRequest {

    private static final String KEY_HOST = "Host";

    private static final String KEY_HTTP_METHOD = "HTTP-METHOD";

    private static final String KEY_QUERY_PARAM_MAP = "HTTP-QUERY-PARAM-MAP";

    private static final String KEY_REQUEST_PATH = "HTTP-REQUEST-PATH";

    private static final String KEY_CONTENT_LENGTH = "Content-Length";

    private static final String HEADER_DELIMITER = ":";

    private final Map<String, Object> headerMap;

    private final Map<String, Object> attributeMap;

    private final Socket client;

    private boolean methodType;

    public HttpRequestImpl(Socket client) {
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("client Socket is null");
        }
        this.client = client;
        this.headerMap = new HashMap<>();
        this.attributeMap = new HashMap<>();
        initialize();
    }

    private void initialize() {
        try {
            // TODO : Web Client 쪽에서 URL 의 값을 변경할 때, 변경된 URL 을 정상적으로 다 입력하기 전에 시동이 들어간다?
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            log.debug("------HTTP-REQUEST_start()");
            firstLineParser(bufferedReader.readLine());

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isNullOrEmpty(line)) {
                    break;
                }
                headerParser(line);
            }

            // Content-Length
            if (methodType) {
                String rawContentLength = getContentLength();
                int contentLength = Integer.parseInt(rawContentLength);

                char[] body = new char[contentLength];
                bufferedReader.read(body);
                parametersParser(new String(body).split("&"));
            }
            log.debug("------HTTP-REQUEST_end()");
        } catch (IOException e) {
            log.debug("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void headerParser(String line) {
        log.debug("{}", line);

        int index = line.indexOf(HEADER_DELIMITER); // String[] data = line.split(":");
        if (index == -1) { return; }

        String key = line.substring(0, index++).trim();
        String value = line.substring(index).trim();
        headerMap.put(key, value);
    }

    private void parametersParser(String[] queryList) {
        Map<String, String> queryMap = getParameterMap();

        for (String query : queryList) {
            String[] parse = query.split("=");
            String key = parse[0].trim();
            String value = parse[1].trim();
            log.debug("[key : {} | value : {}]", key, value);
            queryMap.put(key, value);
        }
        headerMap.put(KEY_QUERY_PARAM_MAP, queryMap);
    }

    private void firstLineParser(String line) {
        log.debug("{}", line);
        boolean queryStringExist = false;
        String httpRequestMethod;
        String httpRequestPath;

        // TODO : URL 조작 도중, client 연결로 인한 null 데이터를 읽어오는 상황을 임시 방지
        if (StringUtils.isNullOrEmpty(line)) { return; }

        if (line.contains("GET") || line.contains("POST")) {
            String[] data = line.split(" ");

            // =========================================================================
            // method
            httpRequestMethod = data[0];
            headerMap.put(KEY_HTTP_METHOD, httpRequestMethod);
            methodType = httpRequestMethod.equals("POST");
            // =========================================================================
            // path
            int urlLastIndex = data[1].length();
            if (data[1].contains("?")) {
                urlLastIndex = data[1].indexOf("?");
                queryStringExist = true;
            }
            httpRequestPath = data[1].substring(0, urlLastIndex);
            headerMap.put(KEY_REQUEST_PATH, httpRequestPath);
            // =========================================================================
            // query
            if (queryStringExist) {
                String[] queryList = data[1].substring(urlLastIndex + 1)
                        .split("&");
                parametersParser(queryList);
            }
            // =========================================================================
        }
    }

    // =================================================================================================================
    // method

    @Override
    public Object getAttribute(String name) { // 원조 기능
        return attributeMap.get(name);
    }

    @Override
    public void setAttribute(String name, Object o) {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("key is Null!");
        }
        if (Objects.isNull(o)) {
            throw new IllegalArgumentException("value is Null!");
        }
        attributeMap.put(name, o);
    }

    @Override
    public Map<String, String> getParameterMap() {
        return Stream.of(headerMap.get(KEY_QUERY_PARAM_MAP))
                        .filter(Map.class::isInstance)
                        .map(o -> (Map<String, String>) o)
                        .findFirst()
                        .orElse(new HashMap<>());   // 만약 한번도 할당한 적이 없다면, 최초 선언
        // .orElseThrow(() -> new IllegalArgumentException("Invalid attribute type"));
    }

    @Override
    public String getParameter(String name) {
        return getParameterMap().get(name);
    }

    @Override
    public String getHost() {
        return getHeader(KEY_HOST);
    }

    @Override
    public String getMethod() {
        return getHeader(KEY_HTTP_METHOD);
    }

    @Override
    public String getRequestURI() {
        return getHeader(KEY_REQUEST_PATH);
    }

    /**
     * HTTP Method POST Request 를 하면, Body 의 length 반환
     *
     * @return 만약, 값이 존재하지 않는 다면, "0" 을 의도적으로 반환
     */
    private String getContentLength() {
        return Optional.ofNullable(getHeader(KEY_CONTENT_LENGTH))
                .orElse("0");
    }

    @Override
    public String getHeader(String name) {
        return Optional.ofNullable(String.valueOf(headerMap.get(name)))
                .orElseThrow(IllegalAccessError::new);
    }
}
