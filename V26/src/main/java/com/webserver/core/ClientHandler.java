package com.webserver.core;

import com.webserver.http.EmptyRequestException;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 该线程任务负责与指定客户完成HTTP交互
 * 1:解析请求
 * 2:处理请求
 * 3:发送响应
 */
public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            //1 解析请求
            HttpServletRequest request = new HttpServletRequest(socket);

            HttpServletResponse response = new HttpServletResponse(socket);
            //2 处理请求
            DispatcherServlet servlet = new DispatcherServlet();
            servlet.server(request, response);
            //3 发送响应
            response.response();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (EmptyRequestException e) {

        } finally {
            //HTTP协议要求,响应完客户端后要断开TCP连接
            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
