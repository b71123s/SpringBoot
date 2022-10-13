package com.tedu.myfirstspringboot.controller;

import com.tedu.myfirstspringboot.entity.Article;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController{
    private static File articleDir;

    static {
        articleDir = new File("./articles");
        if (!articleDir.exists()){
            articleDir.mkdirs();
        }
    }

    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response){
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");

        Article article = new Article(title, author, content);
        File file = new File(articleDir,title+".obj");


        // 如果 標題、作者、內容 不為空則序列畫並跳轉成功頁面
        // 反之 跳轉失敗
        if (title==null||title.trim().isEmpty()||
            author==null||author.trim().isEmpty()||
            content==null||content.trim().isEmpty()
        ){
            try {
                response.sendRedirect("Article_fail.html");
                System.out.println("文章保存失敗!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(
                FileOutputStream fis = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fis);
        ) {
            oos.writeObject(article);
            response.sendRedirect("/Article_success.html");
            System.out.println("已保存文章!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/articleList")
    public void articleList(HttpServletRequest request,HttpServletResponse response){
        System.out.println("開始處理動態數據");
        List<Article> articleList = new ArrayList<>();

        File[] subs = articleDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File sub : subs){
            try(
                    FileInputStream fis = new FileInputStream(sub);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                Article article = (Article)ois.readObject();
                articleList.add(article);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>");
            pw.println("<html lang=\"en\">");
            pw.println("<head>");
            pw.println("<meta charset=\"UTF-8\">");
            pw.println("<title>文章列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>文章列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>文章標題</td>");
            pw.println("<td>作者</td>");
            pw.println("</tr>");

            for (Article article : articleList){
                pw.println("<tr>");
                pw.println("<td>"+article.getTitle()+"</td>");
                pw.println("<td>"+article.getAuthor()+"</td>");
                pw.println("</tr>");
            }

            pw.println("</table>");
            pw.println("</center>");
            pw.println("</body>");
            pw.println("</html>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
