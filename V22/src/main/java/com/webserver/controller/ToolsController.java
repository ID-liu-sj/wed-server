package com.webserver.controller;

import qrcode.QRCodeUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ToolsController {
    public static void main(String[] args) {
        String message = "http://www.bilibili.com";
        try {
            QRCodeUtil.encode(message, "./qr.jpg");

            QRCodeUtil.encode(message,"logo.jpg", new FileOutputStream("./qr.jpg"), true);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
