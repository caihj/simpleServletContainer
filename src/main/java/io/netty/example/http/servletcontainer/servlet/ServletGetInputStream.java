package io.netty.example.http.servletcontainer.servlet;

import sun.misc.IOUtils;
import sun.nio.ch.IOUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletGetInputStream extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletInputStream input = req.getInputStream();
        byte [] arr = IOUtils.readFully(input,-1,true);
        System.out.println("recv");
        System.out.println(req.getContentType());
        System.out.println(new String(arr));
        resp.getWriter().write(new String(arr));
    }
}
