package com.webserver.controller;

import com.sun.media.jfxmedia.events.NewFrameEvent;
import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.core.DispatcherServlet;
import com.webserver.entity.User;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;
import sun.rmi.server.UnicastServerRef;
import sun.security.util.Password;

import java.io.*;
import java.net.URISyntaxException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理与用户相关的业务操作
 */
@Controller
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

    @RequestMapping("/myweb/reg")
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

    @RequestMapping("/myweb/login")
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

    @RequestMapping("/myweb/showAllUser")
    public void showAllUser(HttpServletRequest request, HttpServletResponse response) {
        //1 先用users目录里将所有用户读取出来存入一个List集合备用
        List<User> userList = new ArrayList<>();
        File[] subs = useDir.listFiles(f -> f.getName().endsWith("obj"));//过滤器重点回顾
        for (File file : subs) {
            //if (file.getName().endsWith(".obj")) {
            try (
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                User user = (User) ois.readObject();
                userList.add(user);
                System.out.println("~~~~~~~~~~~~~~~~~~~~" + userList);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            // }
        }
        //2使用程序生成一个页面,同时遍历List集合将用户信息拼接到表格中
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>用户列表</title>");
        pw.println("</head>");
        pw.println("</head>");
        pw.println("<center>");
        pw.println("<h1>用户列表</h1>");
        pw.println("<table border=\"3\">");
        pw.println("<tr>");
        pw.println("<td>用户名</td>");
        pw.println("<td>密码</td>");
        pw.println("<td>昵称</td>");
        pw.println("<td>年龄</td>");
        pw.println("</tr>");
        for (User user : userList) {
            pw.println("<tr>");
            pw.println("<td>" + user.getUsername() + "</td>");
            pw.println("<td>" + user.getPassword() + "</td>");
            pw.println("<td>" + user.getNickname() + "</td>");
            pw.println("<td>" + user.getAge() + "</td>");
            pw.println("</tr>");
        }
        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");
        System.out.println("生成页面完毕!");
        //3 设置响应头Content-Type用于告知动态数据是什么
        response.setContentType("text/html");

    }
}
