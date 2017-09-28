package io.netty.example.http.servletcontainer.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CookieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        System.out.println("Cookie:"+JSON.toJSONString(cookies));
        Cookie cookie = new Cookie("c","d");
        cookie.setMaxAge(10000);
        cookie.setPath("/");
       // cookie.setDomain("127.0.0.1:8080");
        resp.addCookie(cookie);
        resp.getWriter().write("hello cookie"+JSON.toJSONString(cookies));
    }
}
