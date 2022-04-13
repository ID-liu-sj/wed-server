package com.webserver.http;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 响应对象
 * 该类的每一个实例用于表示HTTP协议要求的响应
 * 每个响应由三部分组成:
 * 状态行，响应头，响应正文
 */
public class HttpServletResponse {
    private Socket socket;
    //状态行相关信息
    private int s1 = 200;
    private String s2 = "OK";
    //响应头有相关信息
    private Map<String, String> headers = new HashMap<>();
    //响应正文相关信息
    private File contentFile;

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 将当前响应对象内容按照标准的HTTP响应格式发送给客户端
     */
    public void response() throws IOException {
        //3.1发送状态行
        sendStatusLine();
        //3.2发送响应头
        sendHeaders();
        //3.3发送响应正文(index.html页面的数据)
        sendContent();

    }
    /**
     * 发送状态行
     */
    public void sendStatusLine() throws IOException {
        println("HTTP/1.1 " + s1 + " " + s2);
    }
    /**
     * 发送响应头
     */
    public void sendHeaders() throws IOException {
//        println("Content-Type: text/html");
//        println("Content-Length: " + contentFile.length());
        //遍历headers将所有的响应头发送给客户端
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        for (Map.Entry<String, String> e : entrySet) {
            println(e.getKey() + ": " + e.getValue());
        }
        println("");
    }
    /**
     * 发送响应正文
     */
    public void sendContent() throws IOException {
        OutputStream out = socket.getOutputStream();
        FileInputStream fis = new FileInputStream(contentFile);
        int len;
        byte[] data = new byte[1024 * 10];
        while ((len = fis.read(data)) != -1) {
            out.write(data, 0, len);
        }

    }

    private void println(String line) throws IOException {
        OutputStream out = socket.getOutputStream();
        byte[] data = line.getBytes(StandardCharsets.ISO_8859_1);
        out.write(data);
        out.write(13);
        out.write(10);
    }

    public int getS1() {
        return s1;
    }

    public void setS1(int s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public File getContentFile() {
        return contentFile;
    }

    public void setContentFile(File contentFile) {
        this.contentFile = contentFile;
        String contentType = null;
        try {
            contentType = Files.probeContentType(contentFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*当我们无法根据正文文件确定类型是,则不发送Content-Type
        在HTTP协议中档响应包含正文,但是不包含Content-Type时,则表示让浏览器
        自行判断响应正文内容
         */
        if (contentType != null) {
            addHeader("Content-Type", contentType);
            addHeader("Content-Length", contentFile.length() + "");
        }
    }

    /**
     * 添加一个要发送的响应头
     *
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        //将响应头的名字和值以key-value形式存入headers这个Map中
        headers.put(name, value);
    }
}
