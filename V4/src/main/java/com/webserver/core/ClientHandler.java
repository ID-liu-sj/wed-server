package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
            //读取客户端发过来的消息
            InputStream in = socket.getInputStream();

            char pre = 'a', cur = 'a';//pre上次读取的字符,cur本次读取的字符
            StringBuilder builder = new StringBuilder();
            int d;
            while ((d = in.read()) != -1) {
                cur = (char) d;//本次读取到的字符
                if (pre==13&&cur==10){//判断是否连续读取到了回车和换行符
                    break;
                }
                /*if (cur==' '){
                    break;
                }*/
                builder.append(cur);
                pre = cur;//在进行下次读取字符前将本次读取的字符记作上次读取的字符
            }
            String line = builder.toString().trim();
            String[]data =line.split("\\s");

            System.out.println("merhod:"+data[0]);
            System.out.println("uri:"+data[1]);//这里可能会出现数组下标越界异常!原因:浏览器空请求，后期会解决
            System.out.println("protocol:"+data[2]);

            System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
