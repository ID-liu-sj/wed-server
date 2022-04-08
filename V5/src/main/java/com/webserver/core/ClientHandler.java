package com.webserver.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
            //读取客户端发过来的消息
            //1 解析请求
            //1,1 解析请求行
            String line = readLine();
            System.out.println(line);
            //请求行相关信息
            String[] data = line.split("\\s");//正则表达式
            System.out.println("merhod:" + data[0]);
            System.out.println("uri:" + data[1]);//这里可能会出现数组下标越界异常!原因:浏览器空请求，后期会解决
            System.out.println("protocol:" + data[2]);
            //1.2 解析消息头
            Map<String,String>headers =new HashMap<>();
            while (!(line = readLine()).isEmpty()){
                System.out.println("消息头:" + line);
                //将消息头按照"冒号空格"拆分为消息头的名字和值并以key,value存入header
                data = line.split(":\\s");
                headers.put(data[0],data[1] );

//                System.out.println("标题:"+data[0]);
//                System.out.println("内容:"+data[1]);
            }
            System.out.println("headers:"+headers);





        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过socket获取的输入流读取客户发送过来的一行字符串
     *
     * @return
     */
    private String readLine() throws IOException {
        InputStream in = socket.getInputStream();

        char pre = 'a', cur = 'a';//pre上次读取的字符,cur本次读取的字符
        StringBuilder builder = new StringBuilder();
        int d;
        while ((d = in.read()) != -1) {
            cur = (char) d;//本次读取到的字符
            if (pre == 13 && cur == 10) {//判断是否连续读取到了回车和换行符
                break;
            }
            builder.append(cur);
            pre = cur;//在进行下次读取字符前将本次读取的字符记作上次读取的字符
        }
        return builder.toString().trim();

    }
}
