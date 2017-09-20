package io.netty.example.http.servletcontainer.servlet;

import sun.misc.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class DownloadFile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        File fp = new File("d:\\代理商账号 .txt");
        resp.setContentLength((int) fp.length());
        resp.setContentType("text/plain;charset=gbk");

        OutputStream output = resp.getOutputStream();
        InputStream inputStream = new FileInputStream(fp);
        int d;
        while((d=inputStream.read())>=0){
            output.write(d);
        }
    }
}
