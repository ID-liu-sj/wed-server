package com.webserver.core;

import com.sun.deploy.security.SecureStaticVersioning;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.File;
import java.net.URISyntaxException;

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
        String path = request.getUri();

        File file = new File(staticDir, path);
        System.out.println("资源是否存在:" + file.exists());
        if (file.isFile()) {

            response.setContentFile(file);

        } else {
            response.setS1(404);
            response.setS2("NotFound");
            response.setContentFile(new File(staticDir, "root/404.html"));
        }

    }

}
