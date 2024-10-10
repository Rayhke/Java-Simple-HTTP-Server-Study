package com.nhnacademy.http.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;

@Slf4j
public class ResponseUtils {

    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    public static final String DEFAULT_404 = "/404.html";

    public static final String DEFAULT_405 = "/405.html";

    private ResponseUtils() {}

    // TODO : public 연산자가 아닌 원래는 default 연산자였다.
    // 추후 문제가 생길 수 있으니 참고
    public enum HttpStatus {

        OK(200, "OK"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_FOUND(405, "Method Not Allowed"),
        UNKNOWN(-1, "Unknown Status");

        private final int code;

        private final String description;

        HttpStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static HttpStatus getStatusFromCode(int code) {
            for (HttpStatus status : HttpStatus.values()) {
                if (status.getCode() == code) { return status; }
            }
            return UNKNOWN;
        }
    }

    /**
     * /src/main/resources 하위에 filePath 에 해당되는 파일이 존재하는 체크 합니다.
     *
     * @param filePath, filePath -> requestURl -> ex) /index.html
     * @return true or false
     */
    public static boolean isExist(String filePath) {
        /*
           ex) filePath=/index.html 이면 /resources/index.html 이 존재하면 true, 존재하지 않다면 false를 반환 합니다.
           ex) filePath=/ false 를 반환 합니다.
        */
        // TODO : 실제론 index.html 을 반환해주는 게 맞지만, step05 기준으론 false 로 간주
        if (filePath.equals("/")) { return false; }
        URL url = ResponseUtils.class.getResource(filePath);
        return Objects.nonNull(url);
    }

    /**
     * @param filePath , requestURi, ex) /index.html
     * @return String , index.html 파일을 읽고 String으로 반환
     * @throws IOException
     */
    public static String tryGetBodyFromFile(String filePath) throws IOException {
        /* tryGetBodyFromFile 구현 합니다.
         * ex) filePath = /index.html -> /resources/index.html 파일을 읽어서 반환 합니다.
         * */
        StringBuilder responseBody = new StringBuilder();
        try (InputStream inputStream = ResponseUtils.class.getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_CHARSET_NAME))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
            /*while(true) {
                String line = reader.readLine();
                if(Objects.isNull(line)){
                    break;
                }
                responseBody.append(line);
            }*/
        }
        return responseBody.toString();
    }

    /**
     * @param httpStatusCode , 200 - OK
     * @param charset,       default : UTF-8
     * @param contentLength, responseBody 의 length
     * @return responseHeader 를 String 반환
     */
    public static String createResponseHeader(int httpStatusCode, String charset, int contentLength) {
        /* responseHeader 를 생성 합니다. 아래 header 예시를 참고

            - 200 OK
            HTTP/1.0 200 OK
            Server: HTTP server/0.1
            Content-type: text/html; charset=UTF-8
            Connection: Closed
            Content-Length:143

            - 404 Not Found
            HTTP/1.0 404 Not Found
            Server: HTTP server/0.1
            Content-type: text/html; charset=UTF-8
            Connection: Closed
            Content-Length:143

            - HttpStatusCode는 HttpStatus enum을 참고하여 구현 합니다.
        */

        StringBuilder responseHeader = new StringBuilder();
        responseHeader.append(String.format("HTTP/1.0 %d %s%s", httpStatusCode, HttpStatus.getStatusFromCode(httpStatusCode).getDescription(), StringUtils.CRLF));
        responseHeader.append(String.format("Server: HTTP server/0.1%s", StringUtils.CRLF));
        responseHeader.append(String.format("Content-type: text/html; charset=%s%s", charset, StringUtils.CRLF));
        responseHeader.append(String.format("Connection: Closed%s", StringUtils.CRLF));
        responseHeader.append(String.format("Content-Length:%d %s%s", contentLength, System.lineSeparator(), StringUtils.CRLF));
        return responseHeader.toString();
    }
}
