package io.netty.example.http.servletcontainer.container;

import io.netty.example.http.servletcontainer.util.SimpleUrlMatcher;
import io.netty.example.http.servletcontainer.util.UrlMatcher;

import javax.servlet.http.HttpServlet;
import java.util.HashMap;

/**
 * servlet 映射类支持
 */
public class ServletContainer {

    /**
     * 映射
     */
    private HashMap<String,HttpServlet> servletMap = new HashMap<String, HttpServlet>();

    /**
     * url匹配器
     */
    private UrlMatcher urlMatcher = new SimpleUrlMatcher();


    /**
     * 添加servlet
     * @param urlPattern
     * @param name
     * @param servlet
     */

    public  synchronized void addServlet(String urlPattern, String name, HttpServlet servlet){

        if(servlet==null){
            return;
        }
        servletMap.put(urlPattern,servlet);
    }

    /**
     * 获得可能匹配的servlet
     * @param urlPattern
     * @return
     */
    public synchronized HttpServlet resolveServlet(String urlPattern){

        for( HashMap.Entry<String,HttpServlet> entry:servletMap.entrySet()){
            if(urlMatcher.match(entry.getKey(),urlPattern)){
                return entry.getValue();
            }
        }
        return null;
    }


}
