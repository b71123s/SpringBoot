package com.tedu.myfirstspringboot.controller;


import com.tedu.myfirstspringboot.entity.Article;
import com.tedu.myfirstspringboot.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private static File userDir;

    static {
        userDir = new File("./users");
        if (!userDir.exists()){
            userDir.mkdirs();
        }
    }

    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response){
        System.out.println("開始處理註冊!!!");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");

        // 註冊驗證
        if (username==null||username.trim().isEmpty()||
            password==null||password.trim().isEmpty()||
            nickname==null||nickname.trim().isEmpty()||
            ageStr==null||ageStr.trim().isEmpty()||
            !ageStr.matches("[0-9]+")
        ){
            try {
                response.sendRedirect("/reg_info_error.html");
                return;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        // 驗證正確，執行的動作
        int age =Integer.parseInt(ageStr);
        System.out.println(username+","+password+","+nickname+","+ageStr);

        User user = new User(username,password,nickname,age);
        File file = new File(userDir,username+".obj");

        // 確認用戶是否存在
        if (file.exists()){
            try {
                response.sendRedirect("/have_user.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ){
            oos.writeObject(user);
            System.out.println("寫入成功!");
            response.sendRedirect("reg_success.html");
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request ,HttpServletResponse response){
        System.out.println("開始處理用戶登入...");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 必要信息驗證
        if (username==null||username.trim().isEmpty()||
            password==null||password.trim().isEmpty()){
            //
            try {
                response.sendRedirect("login_info_error.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 新建一個要驗證用的file
        File file = new File(userDir,username+".obj");
        // 存在，讀  ;  不再，到錯誤頁面
        if (file.exists()){
            try(
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User)ois.readObject();
                if (user.getPassword().equals(password)){
                    response.sendRedirect("login_success.html");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            response.sendRedirect("login_fail.html");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/userList")
    public void  userList(HttpServletRequest request,HttpServletResponse response) {
        System.out.println("開始處理動態數據");
        List<User> userList = new ArrayList<>();

        File[] subs = userDir.listFiles(f->f.getName().endsWith(".obj"));
        for(File sub : subs){
            try(
                    FileInputStream fis = new FileInputStream(sub);
                    ObjectInputStream ois = new ObjectInputStream(fis);
            ) {
                User user = (User)ois.readObject();
                System.out.println("已轉型完成");
                userList.add(user);
                System.out.println("已添加進入集合");
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
            pw.println("<title>用戶列表</title>");
            pw.println("</head>");
            pw.println("<body>");
            pw.println("<center>");
            pw.println("<h1>用戶列表</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");
            pw.println("<td>用戶名</td>");
            pw.println("<td>密碼</td>");
            pw.println("<td>暱稱</td>");
            pw.println("<td>年齡</td>");
            pw.println("</tr>");

            for (User user : userList){
                pw.println("<tr>");
                pw.println("<td>"+user.getUsername()+"</td>");
                pw.println("<td>"+user.getPassword()+"</td>");
                pw.println("<td>"+user.getNickname()+"</td>");
                pw.println("<td>"+user.getAge()+"</td>");
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
