package com.webserver.controller;

import com.sun.media.jfxmedia.events.NewFrameEvent;
import com.webserver.core.DispatcherServlet;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import sun.rmi.server.UnicastServerRef;
import sun.security.util.Password;

import java.io.*;
import java.net.URISyntaxException;

/**
 * 处理与用户相关的业务操作
 */
public class UserController {
    private static File useDir;

    /*
      因为全局固定 所以定义成静态
       */
    private static File root;
    private static File staticDir;

    /*
    静态变量需要在静态块里初始化
     */
    static {//对块代码解决异常用Ctrl+Alt+T
        try {
            root = new File(
                    DispatcherServlet.class.getClassLoader().getResource(".").toURI()
            );
            staticDir = new File(root, "static");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        useDir = new File("./users");
        if (!useDir.exists()) {
            useDir.mkdirs();
        }
    }

    public void reg(HttpServletRequest request, HttpServletResponse response) {

        //1 获取用户注册页面上输入的注册信息，获取form表单提交的内容
//requestURI:/myweb/reg
//queryString:username=liusj&password=123456&nickname=awdaw&age=2022-04-13&gender=on
//parameters:{password=123456, gender=on, nickname=awdaw, age=2022-04-13, username=liusj}
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");

        if (username == (null) || password == (null) || nickname == (null) || ageStr == (null) || !ageStr.matches("[0-9]+")) {
            File file = new File(staticDir, "/myweb/reg_input_error.html");
            response.setContentFile(file);
            return;
        }
       /* switch (1) {
            case 1:
                username.equals(null);
                response.setContentFile(file1);
            case 2:
                password.equals(null);
                response.setContentFile(file1);
            case 3:
                nickname.equals(null);
                response.setContentFile(file1);
            case 4:
                ageStr.equals(null) || !ageStr.matches("[0-9]+");
            case 5:

                response.setContentFile(file1);

        }*/

        int age = Integer.parseInt(ageStr);
        System.out.println(username + "/" + password + "/" + nickname + "/" + ageStr);

        //2 将用户信息保存
        File userFile = new File(useDir, username + ".obj");
        if (userFile.exists()) {
            File file = new File(staticDir, "/myweb/have_user.html");
            response.setContentFile(file);
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(userFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            User user = new User(username, password, age, nickname);
            oos.writeObject(user);

            //注册成功了
            File file = new File(staticDir, "/myweb/reg_success.html");
            response.setContentFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3 给用户响应一个注册结果页面(注册成功或注册失败)


    }


    public void login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        File userFile = new File(useDir, username + ".obj");
        if (userFile.exists()) {
            try (FileInputStream fis = new FileInputStream(userFile);
                 ObjectInputStream ois = new ObjectInputStream(fis);) {
                User user = (User) ois.readObject();
                File file;
                if (user.getPassword().equals(password)) {
                    file = new File(staticDir, "/myweb/login_success.html");
                } else {
                    file = new File(staticDir, "/myweb/login_fail.html");
                }
                response.setContentFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            File file = new File(staticDir, "/myweb/login_info_error.html");
            response.setContentFile(file);
        }
    }
}
