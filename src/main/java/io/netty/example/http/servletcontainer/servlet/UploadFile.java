package io.netty.example.http.servletcontainer.servlet;

import sun.misc.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.Collection;

public class UploadFile extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getContentType());

        Collection<Part> parts = req.getParts();
        for(Part t:parts){
            System.out.println(t.getName());
            System.out.println(t.getSize());

            byte [] arr = IOUtils.readFully(t.getInputStream(),-1,true);
            System.out.println(new String(arr));

           // t.write("d:/upload/"+t.getSubmittedFileName());
        }
    }
}
