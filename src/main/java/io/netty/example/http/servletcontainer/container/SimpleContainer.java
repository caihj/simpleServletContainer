package io.netty.example.http.servletcontainer.container;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单容器实现
 */
public class SimpleContainer {

    private Logger logger = LoggerFactory.getLogger(SimpleContainer.class);

    private ServletContainer servletContainer;

    private ServletMapping servletMapping;

    private ServletFactory factory;

    private ServletContext context;


    public SimpleContainer(){
        servletContainer = new ServletContainer();
        servletMapping = new ServletMapping();
        factory = new ServletFactory();
        context = new DefaultServletContext();
    }

    private HttpServlet getServlet(HttpServletRequest request, HttpServletResponse response,String url){

        HttpServlet servlet = servletContainer.resolveServlet(context,url);
        if(servlet==null){
            logger.info("container 没有找到servlet  实例");
        }

        ServletInfo info = servletMapping.getServletInfo(context,url);
        if(info==null){
            //返回404
            logger.info("没有找到serlvetinfo");
            return null;
        }

        final DefaultServletConfig config = new DefaultServletConfig();
        config.setServletContext(context);
        config.setServletName(info.getName());
        config.setInitParams(info.getInitParams());

        servlet = factory.loadServlet(info,config);
        if(servlet==null){
            return null;
        }
        servletContainer.addServlet(info.getUrlPattern(),info.getName(),servlet);
        return servlet;
    }

    public void onRequest(HttpServletRequest request, HttpServletResponse response){

        String uri = request.getRequestURI();
        String url = uri;
        int pos = uri.indexOf("?");
        if(pos>0){
            url = uri.substring(0,pos);
        }

        HttpServlet servlet = getServlet(request,response,url);
        if(servlet==null){
            sendNotFound(request,response);
            return;
        }

        ServletInfo info = servletMapping.getServletInfo(context,url);
        //解析pathinfo
        final String pathInfo = parsePathInfo(info,url,context);

        HttpServletRequest request1 = new HttpServletRequestWrapper(request){
            @Override
            public String getPathInfo() {
                return pathInfo;
            }

            @Override
            public String getContextPath() {
                return context.getContextPath();
            }

        };

        try {
            servlet.service(request1,response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析pathinfo
     * @param info
     */
    private String  parsePathInfo(ServletInfo info,String requestUri,ServletContext context){
        //暂时不实现
        return null;
    }

    private void sendNotFound(HttpServletRequest request, HttpServletResponse response){
        logger.info("发送404");
        try {
            response.setStatus(404);
            response.getWriter().write("not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){

        ServletInfo info = new ServletInfo();
        info.setClassName("io.netty.example.http.servletcontainer.servlet.ServletHelloWorld");
        info.setName("helloServlet");
        info.setUrlPattern("/hello");

        servletMapping.addMapping(info);

        info = new ServletInfo();
        info.setClassName("io.netty.example.http.servletcontainer.servlet.ServletGetInputStream");
        info.setName("post");
        info.setUrlPattern("/post");

        servletMapping.addMapping(info);

        info = new ServletInfo();
        info.setClassName("io.netty.example.http.servletcontainer.servlet.ServletGetParams");
        info.setName("getParams");
        info.setUrlPattern("/getParams");

        info = new ServletInfo();
        info.setClassName("io.netty.example.http.servletcontainer.servlet.UploadFile");
        info.setName("upload");
        info.setUrlPattern("/upload");

        info = new ServletInfo();
        info.setClassName("io.netty.example.http.servletcontainer.servlet.ServletOutPutStream");
        info.setName("output");
        info.setUrlPattern("/output");

        servletMapping.addMapping(info);

    }


}
