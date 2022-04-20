package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.core.DispatcherServlet;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

@Controller
public class ToolsController {
    @RequestMapping("/myweb/createQR")
    public void createQr(HttpServletRequest request, HttpServletResponse response) {
        String content = request.getParameter("content");
        try {
            QRCodeUtil.encode(content, "logo.jpg", response.getOutputStream(), true);
            response.setContentType("image/jpeg");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws URISyntaxException {

        File fileDir = new File(DispatcherServlet.class.getClassLoader().getResource("./com/webserver/controller").toURI());

        System.out.println(fileDir);



        /*String message = "http://www.bilibili.com";
        try {
            //参数1:二维码上包含的文本信息  参数2:图片生成的位置
//            QRCodeUtil.encode(message,"./qr.jpg");
            //参数1:二维码上包含的文本信息  参数2:图片生成后会通过该流写出
//            QRCodeUtil.encode(message,new FileOutputStream("./qr.jpg"));
            //参数1:二维码上包含的文本信息  参数2:二维码中间的logo图片 参数3:图片生成的位置 参数4:是否需要压缩logo图片到中间大小
//            QRCodeUtil.encode(message,"logo.jpg","./qr.jpg",true);

            QRCodeUtil.encode(message, "logo.jpg", new FileOutputStream("./qr.jpg"), true);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
