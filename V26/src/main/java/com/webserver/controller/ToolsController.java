package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;

import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import qrcode.QRCodeUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @RequestMapping("/myweb/random.jpg")
    public void createRandomImage(HttpServletRequest request, HttpServletResponse response) {
        //验证码模式
        //1创建一张空白图片,并且指定宽高
        BufferedImage image = new BufferedImage(70, 30, BufferedImage.TYPE_3BYTE_BGR);

        Graphics g = image.getGraphics();
        String line = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM0987654321";
        Random random = new Random();
        Color bgcolor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        g.setColor(bgcolor);
        g.fillRect(0, 0, 70, 30);

        for (int i = 0; i < 4; i++) {
            String str = line.charAt(random.nextInt(line.length())) + "";
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            g.setColor(color);
            g.setFont(new Font(null, Font.BOLD, 20));
            g.drawString(str, i * 15 + 5, 18 + random.nextInt(11) - 5);
        }

        //随机生成4条干扰线
        for (int i = 0; i < 4; i++) {
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            g.drawLine(random.nextInt(71), random.nextInt(31), random.nextInt(71), random.nextInt(31));

        }


        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
            response.setContentType("image/jpeg");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws URISyntaxException {

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

        //验证码模式
        //1创建一张空白图片,并且指定宽高
        BufferedImage image = new BufferedImage(70, 30, BufferedImage.TYPE_3BYTE_BGR);

        Graphics g = image.getGraphics();
        String line = "qwertyuioplkjhgfdsazxcvbnmQWERTYUIOPLKJHGFDSAZXCVBNM0987654321";
        Random random = new Random();
        Color bgcolor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        g.setColor(bgcolor);
        g.fillRect(0, 0, 70, 30);

        for (int i = 0; i < 4; i++) {
            String str = line.charAt(random.nextInt(line.length())) + "";
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            g.setColor(color);
            g.setFont(new Font(null, Font.BOLD, 20));
            g.drawString(str, i * 15 + 5, 18 + random.nextInt(11) - 5);
        }

        //随机生成4条干扰线
        for (int i = 0; i < 4; i++) {
            Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            g.drawLine(random.nextInt(71), random.nextInt(31), random.nextInt(71), random.nextInt(31));

        }


        try {
            ImageIO.write(image, "jpg", new FileOutputStream("./random.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


//        List<String> list = new ArrayList<>();
//        list.get(1);


    }
}
