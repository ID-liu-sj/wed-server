package com.webserver.core;

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
            //通过请求对象,获取浏览器地址栏中的抽象路径部分
            String path = request.getUri();
            File root = new File(
                    ClientHandler.class.getClassLoader().getResource(".").toURI()
            );
            File staticDir = new File(root, "static");
            File file = new File(staticDir, path);
            System.out.println("资源是否存在:" + file.exists());
            if (file.isFile()) {

                response.setContentFile(file);

            } else {
                response.setS1(404);
                response.setS2("NotFound");
                response.setContentFile(new File(staticDir, "root/404.html"));
            }
            //3 发送响应
            response.response();

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
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
