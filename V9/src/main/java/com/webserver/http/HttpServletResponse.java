package com.webserver.http;

import com.webserver.core.ClientHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class HttpServletResponse {
    private Socket socket;
    //状态行相关信息
    private int s1 = 200;
    private String s2 = "OK";
    //响应行有相关信息

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

    public void sendStatusLine() throws IOException {
        println("HTTP/1.1 " + s1 + " " + s2);
    }

    public void sendHeaders() throws IOException {
        println("Content-Type: text/html");
    }

    public void sendContent() throws IOException {
        println("Content-Length: " + contentFile.length());
        OutputStream out = socket.getOutputStream();
        //println("");
        out.write(13);
        out.write(10);
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
    }
}
