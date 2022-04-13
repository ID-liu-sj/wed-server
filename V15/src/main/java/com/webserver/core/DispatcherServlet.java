package com.webserver.core;

import com.sun.deploy.security.SecureStaticVersioning;
import com.webserver.controller.UserController;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

/**
 * 用于处理请求
 */
public class DispatcherServlet {
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
    }

    public void server(HttpServletRequest request
            , HttpServletResponse response) {

        //通过请求对象,获取浏览器地址栏中的抽象路径部分
        String path = request.getRequestURI();
        //首先判断该请求是否为请求一个业务,
        //因为分支结构所以不会同时发送正文内容
        //就会导致setContentFile()空指针
        if ("/myweb/reg".equals(path)) {
            System.out.println("!!!!!!!!开始处理注册");
            UserController controller = new UserController();
            controller.reg(request,response);


        } else if ("/myweb/login".equals(path)) {
            System.out.println("!!!!!!!!!!!开始处理登录");
        } else {
            File file = new File(staticDir, path);
            System.out.println("资源是否存在:" + file.exists());
            if (file.isFile()) {
                response.setContentFile(file);
            } else {//要么file表示的是一个目录,要么不存在
                response.setS1(404);
                response.setS2("NotFound");
                file = new File(staticDir, "root/404.html");
                response.setContentFile(file);
            }
        }
        //添加一个额外的响应头
        response.addHeader("Server", "WebServer");

    }

}
