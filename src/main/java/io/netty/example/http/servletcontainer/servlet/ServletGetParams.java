package io.netty.example.http.servletcontainer.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class ServletGetParams extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String,String []> stringMap = req.getParameterMap();
        System.out.println(JSON.toJSONString(stringMap));
        resp.getWriter().write("ok");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String,String []> stringMap = req.getParameterMap();
        System.out.println(JSON.toJSONString(stringMap));
        resp.getWriter().write("ok");
    }
}
