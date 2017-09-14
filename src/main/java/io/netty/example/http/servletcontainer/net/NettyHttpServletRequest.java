package io.netty.example.http.servletcontainer.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.example.http.servletcontainer.util.GenTools;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * HTTP request 实现类，封装netty的消息
 */
public class NettyHttpServletRequest implements HttpServletRequest {

    private FullHttpRequest msg;

    private ChannelHandlerContext ctx;

    private Map<String,Object> attributes = new HashMap<String, Object>();

    private String characterEncoding ;

    private Map<String,String> paramsMap;

    public NettyHttpServletRequest(ChannelHandlerContext ctx,FullHttpRequest msg){
        this.ctx = ctx;
        this.msg = msg;
    }

    /**
     * 返回认证方式
     * @return
     */
    @Override
    public String getAuthType() {
        return null;
    }


    /**
     * 返回cookie
     * @return
     */
    @Override
    public Cookie[] getCookies() {

        Cookie [] cookies = null;

        Set<io.netty.handler.codec.http.cookie.Cookie> cookies1 = ServerCookieDecoder.STRICT.decode(msg.headers().get(HttpHeaderNames.COOKIE));
        if(cookies1!=null && cookies1.size()>0){
            cookies = new Cookie[cookies1.size()];
            int i=0;
            for(io.netty.handler.codec.http.cookie.Cookie cookie:cookies1){
                Cookie c = new Cookie(cookie.name(),cookie.value());
                cookies[i++]=c;
            }
        }

        return cookies;
    }

    @Override
    public long getDateHeader(String name) {
        return msg.headers().getTimeMillis(name);
    }

    @Override
    public String getHeader(String name) {
        return msg.headers().get(name);
    }

    @Override
    public Enumeration getHeaders(String name) {
        List<String> value = msg.headers().getAll(name);
        if(value!=null) {
            return GenTools.iteratorToEnumeration(value.iterator());
        }

        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        Set<String> set = msg.headers().names();
        if(set!=null) {
            return GenTools.iteratorToEnumeration(set.iterator());
        }
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return msg.headers().getInt(name);
    }

    @Override
    public String getMethod() {
        return msg.method().name();
    }

    /**
     * 在container层 包裹实现
     * @return
     */
    @Override
    public String getPathInfo() {
        return null;
    }

    /**
     * 暂不实现
     * @return
     */
    @Override
    public String getPathTranslated() {
        return null;
    }

    /**
     * 在container层 包裹实现
     * @return
     */
    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
       String uri = msg.uri();
       int pos = uri.indexOf('?');
       if(pos>0){
            return uri.substring(pos+1);
       }else {
           return null;
       }
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return msg.uri();
    }

    @Override
    public StringBuffer getRequestURL() {

        String host = msg.headers().get(HttpHeaderNames.HOST);
        if(host==null){
            SocketAddress address = ctx.channel().localAddress();
            if(address instanceof InetSocketAddress){
                InetSocketAddress ad = (InetSocketAddress) address;
                host = ad.getHostName()+":"+ad.getPort();
            }else{
                host="localhost";
            }
        }

        return new StringBuffer("http://"+host+msg.uri());
    }

    /**
     * 在container层 包裹实现
     * @return
     */
    @Override
    public String getServletPath() {
        return null;
    }

    //session 先不实现
    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }


    //先不实现
    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    //先不实现
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    //先不实现
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    //先不实现
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }


    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {

        final Iterator<Map.Entry<String, Object>> iterable = attributes.entrySet().iterator();

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
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        Charset.forName(env);
        characterEncoding = env;
    }

    @Override
    public int getContentLength() {
        Integer length = msg.headers().getInt(HttpHeaderNames.CONTENT_LENGTH);
        if(length==null){
            return  -1;
        }else{
            return length;
        }
    }

    @Override
    public String getContentType() {
        return msg.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }


    @Override
    public String getProtocol() {
        return msg.protocolVersion().text();
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        String host = msg.headers().get(HttpHeaderNames.HOST);
        if(host==null){
            SocketAddress address = ctx.channel().localAddress();
            if(address instanceof InetSocketAddress){
                InetSocketAddress ad = (InetSocketAddress) address;
                host = ad.getHostName();
            }else{
                host="localhost";
            }
        }else{
            host=host.split(":")[0];
        }
        return host;
    }

    @Override
    public int getServerPort() {
        String host = msg.headers().get(HttpHeaderNames.HOST);
        int port=80;
        if(host==null){
            SocketAddress address = ctx.channel().localAddress();
            if(address instanceof InetSocketAddress){
                InetSocketAddress ad = (InetSocketAddress) address;
               port = ad.getPort();
            }
        }else{
            String [] arr=host.split(":");
            if(arr.length>1){
                return Integer.parseInt(arr[1]);
            }
        }
        return port;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        SocketAddress address = ctx.channel().remoteAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getAddress().getHostAddress();
        }else {
            return null;
        }
    }

    @Override
    public String getRemoteHost() {
        SocketAddress address = ctx.channel().remoteAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getHostName();
        }else {
            return null;
        }
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name,o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {

        SocketAddress address = ctx.channel().remoteAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getPort();
        }else {
            return -1;
        }
    }

    @Override
    public String getLocalName() {
        SocketAddress address = ctx.channel().localAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getHostName();
        }else {
            return null;
        }
    }

    @Override
    public String getLocalAddr() {
        SocketAddress address = ctx.channel().localAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getAddress().getHostAddress();
        }else {
            return null;
        }
    }

    @Override
    public int getLocalPort() {
        SocketAddress address = ctx.channel().localAddress();
        if(address instanceof InetSocketAddress){
            InetSocketAddress ad = (InetSocketAddress) address;
            return ad.getPort();
        }else {
            return -1;
        }
    }
}
