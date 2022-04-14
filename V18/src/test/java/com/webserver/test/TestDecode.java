package com.webserver.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class TestDecode {
    public static void main(String[] args) {
        String line = "/myweb/login?username=%E5%88%98&password=123456";
        try {
            line = URLDecoder.decode(line, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(line);
    }



}
