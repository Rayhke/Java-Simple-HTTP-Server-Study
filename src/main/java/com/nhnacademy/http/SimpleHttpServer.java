package com.nhnacademy.http;

import com.nhnacademy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class SimpleHttpServer {

    // 운영체제에 따라 줄바꿈을 하는 방식이 다름
    private static final String CRLF = System.lineSeparator(); //"\r\n";

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private final ServerSocket serverSocket;

    public SimpleHttpServer() {
        this(DEFAULT_PORT);
    }

    public SimpleHttpServer(int port) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException(String.format("port is range Out! : %d", port));
        }
        this.port = port;

        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // =================================================================================================================

    public void start() throws IOException {
        while (true) {
            try (Socket client = serverSocket.accept();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))
            ) {
                StringBuilder requestBuilder = new StringBuilder();
                log.debug("------HTTP-REQUEST_start()");
                while (true) {
                    String line = bufferedReader.readLine();
                    // requestBuilder 에 append 합니다.
                    requestBuilder.append(line);
                    log.debug("{}", line);

                    // 종료 조건 : null or size==0
                    if (StringUtils.isNullOrEmpty(line)) {
                        break;
                    }
                }
                log.debug("------HTTP-REQUEST_end()");

                // client 에 응답할 html 을 작성합니다.
                /*
                    <html>
                        <body>
                            <h1>hello hava</h1>
                        </body>
                    </html>
                */

                StringBuilder responseBody = new StringBuilder();
                responseBody.append("<html>");
                responseBody.append("<body>");
                responseBody.append("<h1>hello java</h1>");
                responseBody.append("</body>");
                responseBody.append("</html>");

                StringBuilder responseHeader = new StringBuilder();

                // HTTP/1.0 200 OK
                responseHeader.append(String.format("HTTP/1.0 200 OK%s", CRLF));

                responseHeader.append(String.format("Server: HTTP server/0.1%s", CRLF));

                // Content-type: text/html; charset=UTF-8"
                responseHeader.append(String.format("Content-type: text/html; charset=%s%s", "UTF-8", CRLF));


                // Connection: close 헤더를 사용하면 해당 요청 후에 연결이 닫히게 된다.
                responseHeader.append(String.format("Connection: Closed%s", CRLF));

                // responseBody 의 Content-Length 를 설정 합니다.
                responseHeader.append(String.format("Content-Length:%d %s%s", responseBody.toString().getBytes().length, CRLF, CRLF));

                // write Response Header
                bufferedWriter.write(responseHeader.toString());

                // write Response Body
                bufferedWriter.write(responseBody.toString());

                // buffer 에 등록된 Response (header, body) flush 합니다.(socket 을 통해서 clent 에 응답 합니다.)
                bufferedWriter.flush();

                log.debug("header : {}", responseHeader);
                log.debug("body : {}", responseBody);

            } catch (IOException e) {
                log.error("socket error : {}", e.getMessage(), e);
            }
        } //end while
    }
}
