package com.webserver.http;


import java.io.*;
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

    private ByteArrayOutputStream byteArrayOutputStream;//目前会通过sendBefore()把值传递给contentData
    private byte[] contentData;//作为响应正文数据的字节数组

    public HttpServletResponse(Socket socket) {
        this.socket = socket;
    }

    /**
     * 将当前响应对象内容按照标准的HTTP响应格式发送给客户端
     */
    public void response() throws IOException {
        //发送前的准备工作
        sendBefore();
        //3.1发送状态行
        sendStatusLine();
        //3.2发送响应头
        sendHeaders();
        //3.3发送响应正文
        sendContent();

    }

    /**
     * 发送前的最后准备工作
     */
    private void sendBefore() {
        //先判断byteArrayOutputStream是否为null,如果不是null则说明存在动态数据
        if (byteArrayOutputStream != null) {
            //得到其内部的字节数组(动态数据)
            contentData = byteArrayOutputStream.toByteArray();
        }
        //如果动态数据数组不为null,添加响应头Content-Length,目前暂没有应用
        if (contentData != null) {
            addHeader("Content-Length", contentData.length + "");
        }

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
        if (contentData != null) {//有动态数据
            OutputStream out = socket.getOutputStream();
            out.write(contentData);
        } else if (contentFile != null) {
            OutputStream out = socket.getOutputStream();
            try (FileInputStream fis = new FileInputStream(contentFile)) {//自动关闭特性try(){}
                int len;
                byte[] data = new byte[1024 * 10];
                while ((len = fis.read(data)) != -1) {
                    out.write(data, 0, len);
                }
            }
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

    /**
     * 返回一个字节输出流,通过这个写出的数据最终作为响应正文内容发送给浏览器
     * 只要响应对象相同,无论调用多少次该方法,返回的始终是同一个输出流.
     */
    public OutputStream getOutputStream() {
        if (byteArrayOutputStream == null) {
            byteArrayOutputStream = new ByteArrayOutputStream();
        }
        return byteArrayOutputStream;
    }

    /**
     * 通过该方法获取缓冲字符输出流写出的文本会作为正文内容发送给浏览器
     */
    public PrintWriter getWriter() {
        return new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                getOutputStream(),
                                StandardCharsets.UTF_8
                        )
                ), true
        );
    }

    /**
     * 设置响应头Content_Type的值
     *
     * @param mime
     */
    public void setContentType(String mime) {
        addHeader("Content-Type", mime);
    }

}
