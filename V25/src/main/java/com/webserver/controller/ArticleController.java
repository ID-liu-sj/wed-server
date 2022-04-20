package com.webserver.controller;

import com.webserver.annotation.Controller;
import com.webserver.annotation.RequestMapping;
import com.webserver.core.DispatcherServlet;
import com.webserver.entity.Article;
import com.webserver.http.HttpServletRequest;
import com.webserver.http.HttpServletResponse;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    private static File articlesDir;

    /*
      因为全局固定 所以定义成静态
       */


    /*
    静态变量需要在静态块里初始化
     */
    static {
        articlesDir = new File("./articles");
        if (!articlesDir.exists()) {
            articlesDir.mkdirs();
        }
    }

    @RequestMapping("/myweb/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response) {
        //获取用户输入的标题与正文
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String author = request.getParameter("author");

        if (title == null || content == null || author == null) {//与null比较不要用equals()就会空指针
//            File file = new File(staticDir, "/myweb/article_fail.html");
//            response.setContentFile(file);
            response.sendRedirect("/myweb/article_fail.html");
            return;//用于跳出writeArticle()方法
        }
        File articleFile = new File(articlesDir, title + ".obj");
        try (
                FileOutputStream fos = new FileOutputStream(articleFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            Article article = new Article(title, author, content);
            oos.writeObject(article);

            //发表成功后序列化
//            File file = new File(staticDir, "/myweb/article_success.html");
//            response.setContentFile(file);
            response.sendRedirect("/myweb/article_success.html");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/myweb/showAllArticle")
    public void showAllArticle(HttpServletRequest request, HttpServletResponse response) {
        List<Article> articleList = new ArrayList<>();
        File[] subs = articlesDir.listFiles(f -> f.getName().endsWith(".obj"));
        for (File af : subs) {
            try (
                    FileInputStream fis = new FileInputStream(af);
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                articleList.add((Article) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        PrintWriter pw = response.getWriter();
        pw.println("<!DOCTYPE html>");
        pw.println("<html lang=\"en\"><html lang=\"en\">");
        pw.println("<head>");
        pw.println("<meta charset=\"UTF-8\">");
        pw.println("<title>文章列表</title>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<center>");
        pw.println("<h1>文章列表</h1>");
        pw.println("<table border=\"3\">");
        pw.println("<tr>");
        pw.println("<td>标题</td>");
        pw.println("<td>作者</td>");
        pw.println("<td>操作</td>");
        pw.println("</tr>");
        for (Article article : articleList) {
            pw.println("<tr>");
            pw.println("<td>" + article.getTitle() + "</td>");
            pw.println("<td>" + article.getAuthor() + "</td>");
            pw.println("<td><a href='/myweb.deleteArticle?title=" + article.getTitle() + "'>删除</a></td>");

            pw.println("</tr>");
        }
        pw.println("</table>");
        pw.println("</center>");
        pw.println("</body>");
        pw.println("</html>");
        response.setContentType("text/html");
    }

    @RequestMapping("/myweb.deleteArticle")
    public void deleteArticle(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        File articleFile = new File(articlesDir, title + ".obj");
        articleFile.delete();
        response.sendRedirect("/myweb/showAllArticle");
    }


}
