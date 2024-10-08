package com.nhnacademy.http.request.impl;

import com.nhnacademy.http.request.HttpRequest;
import com.nhnacademy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class HttpRequestImpl implements HttpRequest {

    private static final String KEY_HTTP_METHOD = "HTTP-METHOD";
    private static final String KEY_QUERY_PARAM_MAP = "HTTP-QUERY-PARAM-MAP";
    private static final String KEY_REQUEST_PATH = "HTTP-REQUEST-PATH";
    private static final String HEADER_DELIMITER = ":";

    private final Map<String, Object> headerMap;

    private final Map<String, Object> attributeMap;

    private final Socket client;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            log.debug("------HTTP-REQUEST_start()");
            firstLineParser(bufferedReader.readLine());

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (StringUtils.isNullOrEmpty(line)) {
                    break;
                }
                log.debug("{}", line);
                String[] data = line.split(HEADER_DELIMITER);
                setAttribute(data[0].trim(), data[1].trim());
            }
            log.debug("------HTTP-REQUEST_end()");
        } catch (IOException e) {
            log.debug("{}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void firstLineParser(String line) {
        log.debug("{}", line);
        boolean queryStringExist = false;
        String httpRequestMethod;
        String httpRequestPath;
        Map<String, String> queryMap = new HashMap<>();

        if (line.contains("GET") || line.contains("POST")) {
            String[] data = line.split(" ");

            // =========================================================================
            // method
            httpRequestMethod = data[0];
            setAttribute(KEY_HTTP_METHOD, httpRequestMethod);
            // =========================================================================
            // path
            int urlLastIndex = data[1].length();
            if (data[1].contains("?")) {
                urlLastIndex = data[1].indexOf("?");
                queryStringExist = true;
            }
            httpRequestPath = data[1].substring(0, urlLastIndex);
            setAttribute(KEY_REQUEST_PATH, httpRequestPath);
            // =========================================================================
            // query
            if (queryStringExist) {
                String[] queryList = data[1].substring(urlLastIndex + 1)
                        .split("&");
                for (String query : queryList) {
                    String[] parse = query.split("=");
                    String key = parse[0];
                    String value = parse[1];
                    log.debug("[key : {} | value : {}]", key, value);
                    queryMap.put(key, value);
                }
                setAttribute(KEY_QUERY_PARAM_MAP, queryMap);
            }
            // =========================================================================
        }
    }

    // =================================================================================================================
    // method

    @Override
    public String getMethod() {
        return getParameter(KEY_HTTP_METHOD);
    }

    @Override
    public String getParameter(String name) {
        return (String) getAttribute(name);
    }

    @Override
    public Map<String, String> getParameterMap() {
        return Stream.of(getAttribute(KEY_QUERY_PARAM_MAP))
                .filter(Map.class::isInstance) // (o -> o instanceof Map)
                .map(o -> (Map<String, String>) o)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid attribute type"));
    }

    @Override
    public String getHeader(String name) {
        return "";
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
    public Object getAttribute(String name) { // 원조 기능
        return attributeMap.get(name);
    }

    @Override
    public String getRequestURI() {
        return getParameter(KEY_REQUEST_PATH);
    }
}
