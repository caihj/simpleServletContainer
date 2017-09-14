package io.netty.example.http.servletcontainer.container;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class ServletFactory {

    /**
     * 加载servlet
     * @param info
     * @return
     */

    public HttpServlet loadServlet(ServletInfo info,ServletConfig config){
        String className = info.getClassName();
        try {
            Class cls = Class.forName(className);
            Object servlet = cls.newInstance();
            if(servlet instanceof HttpServlet){
                ((HttpServlet) servlet).init(config);
                return (HttpServlet) servlet;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        return null;
    }
}
