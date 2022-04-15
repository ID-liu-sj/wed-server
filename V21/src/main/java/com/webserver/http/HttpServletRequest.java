package com.webserver.http;

import org.apache.log4j.pattern.IntegerPatternConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.InflaterInputStream;

/**
 * HTTP请求对象
 * 该类的每一个实例用于表示一个HTTP请求内容
 * 每个请求由三部分构成:
 * 请求行，消息头，消息正文
 */
public class HttpServletRequest {
    private Socket socket;
    /*请求行相关信息*/
    private String method;//请求方式
    private String uri;//抽象路径


    //保存uri中"?"左侧的内容.如果uri中没有"?"则内容与uri一致
    private String requestURI;
    //保存uri中"?"右侧的内容,即:参数部分
    private String queryString;
    //用来保存客户端传递过来的每一组参数
    private Map<String, String> parameters = new HashMap<>();


    private String protocol;//协议版本
    /*消息头相关信息*/
    private Map<String, String> headers = new HashMap<>();

    public HttpServletRequest(Socket socket) throws IOException, EmptyRequestException {
        this.socket = socket;

        //1,1 解析请求行
        parseRequestLine();

        //1.2 解析消息头
        parseHeaders();

        //1.3解析消息正文
        parseContent();

    }

    /**
     * 解析请求行
     */
    private void parseRequestLine() throws IOException, EmptyRequestException {
        String line = readLine();
        if (line.isEmpty()) {//如果请求行为空字符串,则说明为空请求
            throw new EmptyRequestException();
        }
        //System.out.println(line);
        //请求行相关信息
        String[] data = line.split("\\s");//正则表达式
        method = data[0];
        uri = data[1];
        protocol = data[2];
        //进一步解析uri
        parseUri();
        System.out.println("method:" + method);
        System.out.println("uri:" + uri);//这里可能会出现数组下标越界异常!原因:浏览器空请求，后期会解决
        System.out.println("protocol:" + protocol);

    }

    /**
     * 进一步解析uri
     */
    // http://localhost:8088
    // /myweb/reg?username=liusj&password=%3Biu19960520&nickname=niad&age=27
    private void parseUri() {
         /*
            uri是有两种情况的，1:不含有参数的 2:含有参数的
            例如:
            不含有参数的:/myweb/reg.html
            含有参数的:/myweb/reg?username=fanchuanqi&password=123456&nickname=chuanqi&age=22

            处理方式:
            1:若不含有参数，则直接将uri的值赋值给requestURI
            2:若含有参数
              2.1:先将uri按照"?"拆分为请求部分和参数部分
                  将请求部分赋值给requestURI
                  将参数部分赋值给queryString
              2.2:再将参数部分按照"&"拆分出每一组参数
                  每组参数再按照"="拆分为参数名和参数值
                  并将参数名作为key，参数值作为value保存到parameters这个Map中

              允许页面输入框空着，这种情况该参数的值为null存入parameters即可
         */
        String[] data = uri.split("\\?");
        requestURI = data[0];
        if (data.length > 1) {//说明?后面有参数部分
            //username=fanchuanqi&password=123456&nickname=chuanqi&age=22
            queryString = data[1];
            //进一步拆分参数部分
           parseParameter(queryString);
        }

        System.out.println("requestURI:" + requestURI);
        System.out.println("queryString:" + queryString);
        System.out.println("parameters:" + parameters);
    }

    /**
     * 解析消息头
     */
    private void parseHeaders() throws IOException {
        String line;
        while (!(line = readLine()).isEmpty()) {
            System.out.println("消息头:" + line);
            //将消息头按照"冒号空格"拆分为消息头的名字和值并以key,value存入header
            String[] data = line.split(":\\s");
            headers.put(data[0].toLowerCase(), data[1]);
        }
        //System.out.println("headers:" + headers);
    }

    /**
     * 解析消息正文
     */
    private void parseContent() throws IOException {
        //当一个请求的请求方式为POST时,则说明应当会包含消息正文
        if ("post".equalsIgnoreCase(method)) {
            //消息头中是否包含Content-Length
            if (headers.containsKey("content-length")) {
                int contentLength = Integer.parseInt(
                        headers.get("content-length")
                );
  //              System.out.println(">>>>>>>>>>>>>>>>>>" + contentLength);
                byte[] data = new byte[contentLength];
                InputStream inputStream = socket.getInputStream();
                inputStream.read(data);

                //根据消息头Content_Type判断正文类型并进行对应的解析
                if (headers.containsKey("content-type")) {
                    String contentType = headers.get("content-type");
 //                   System.out.println("======================"+contentType);
                    if ("application/x-www-form-urlencoded".equals(contentType)) {
                        String line = new String(data, StandardCharsets.ISO_8859_1);
                        System.out.println("正文内容=============" + line);
                        parseParameter(line);


                    }
 //                   else if () { }
                }


            }
        }

    }

    private void parseParameter(String line) {
        try {
            line = URLDecoder.decode(line, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[]data = line.split("&");
        //遍历数组，再进一步拆分每一个参数
        for (String a : data) {
            //[username, xxxx]
            String[] b = a.split("=");
            parameters.put(b[0], b.length > 1 ? b[1] : null);
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

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }

    /*
        根据消息头的名字获取对应消息头的值
     */
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
