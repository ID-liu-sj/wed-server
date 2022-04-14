package com.webserver.controller;

import com.webserver.core.DispatcherServlet;
import com.webserver.entity.Article;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.URISyntaxException;

public class ArticleController {
    private static File articlesDir;

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
        articlesDir = new File("./articles");
        if (!articlesDir.exists()) {
            articlesDir.mkdirs();
        }
    }


    public void writeArticle(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        if (title == (null) || content == (null)) {
            File file = new File(staticDir, "/myweb/article_fail.html");
            response.setContentFile(file);
            return;
        }
        File articleFile = new File(articlesDir, title + ".obj");
        try (
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            Article article = new Article(title, content);
            oos.writeObject(article);
            File file = new File(staticDir, "/myweb/article_success.html");
            response.setContentFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
