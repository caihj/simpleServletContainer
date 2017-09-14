package io.netty.example.http.servletcontainer.container;

import io.netty.example.http.servletcontainer.util.GenTools;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class DefaultServletConfig implements ServletConfig {

    private String servletName;

    private ServletContext servletContext;

    private Map<String,String> initParams;

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String name) {
        return initParams.get(name);
    }

    @Override
    public Enumeration getInitParameterNames() {
        if(initParams!=null){
            final Iterator<Map.Entry<String, String>> iterable = initParams.entrySet().iterator();
              return new Enumeration() {
                @Override
                public boolean hasMoreElements() {
                    return iterable.hasNext();
                }

                @Override
                public Object nextElement() {
                    return iterable.next().getKey();
                }
            };
        } else{
            return null;
        }
    }
}
