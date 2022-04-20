package com.webserver.core;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.controller.ArticleController;
import com.webserver.controller.ToolsController;
import com.webserver.controller.UserController;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Map;

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
        System.out.println("====================>" + path);
        //首先判断该请求是否为请求一个业务
        try {
            HandlerMapping.MethodMapping mm = HandlerMapping.getMethod(path);
            if (mm != null) {
                Object controller = mm.getController();
                Method method = mm.getMethod();
                method.invoke(controller, request, response);
                return;//跳出当前server()方法,不再走下面代码,不然最后都会404
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        File file = new File(staticDir, path);
        System.out.println("资源是否存在:" + file.exists());
        if (file.isFile()) {//当file表示的文件真实存在且是一个文件时返回true
            response.setContentFile(file);
        } else {//要么file表示的是一个目录,要么不存在
            response.setStatusCode(404);
            response.setStatusReason("NotFound");
            file = new File(staticDir, "root/404.html");
            response.setContentFile(file);
        }
        //添加一个额外的响应头
        response.addHeader("Server", "WebServer");
    }
}




