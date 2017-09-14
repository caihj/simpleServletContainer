package io.netty.example.http.servletcontainer.container;

import io.netty.example.http.servletcontainer.util.SimpleUrlMatcher;
import io.netty.example.http.servletcontainer.util.UrlMatcher;

import java.util.HashMap;
import java.util.Map;

public class ServletMapping {

    private Map<String,ServletInfo> configMap = new HashMap<String, ServletInfo>();

    /**
     * url匹配器
     */
    private UrlMatcher urlMatcher = new SimpleUrlMatcher();

    public void addMapping(ServletInfo config){
        configMap.put(config.getUrlPattern(),config);
    }

    /**
     * 获取匹配的servlet
     * @param urlPattern
     * @return
     */
    public ServletInfo getServletInfo(String urlPattern){

        for( HashMap.Entry<String,ServletInfo> entry:configMap.entrySet()){
            if(urlMatcher.match(entry.getKey(),urlPattern)){
                return entry.getValue();
            }
        }
        return null;
    }
}
