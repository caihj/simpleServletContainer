package io.netty.example.http.servletcontainer.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.example.http.servletcontainer.handler.HttpRequestHandler;
import io.netty.example.http.servletcontainer.util.GenTools;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;
import java.io.*;
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

    private HttpRequest msg;

    private ChannelHandlerContext ctx;

    private Map<String,Object> attributes = new HashMap<String, Object>();

    private String characterEncoding ;

    private Map<String, List<String>> paramsMap;

    private ServletContext servletContext;

    /**
     * 是否已经解析 params
     */
    private boolean parsePararms = false;

    /**
     * 是否已经获取流
     */
    private boolean getInputStream = false;

    /**
     * 保存 post 上来的所有数据
     * @param ctx
     * @param msg
     */
    private List<InterfaceHttpData> parts;

    private Map<String,Part> partMap = null;

    /**
     * 保存除文件上传外的其他数据
     */
    private FileUpload  httpData;

    public NettyHttpServletRequest(ChannelHandlerContext ctx,HttpRequest msg){
        this.ctx = ctx;
        this.msg = msg;
        //解析queryString

    }

    /**
     * 解析queryString
     */
    public void parseQueryString()  {

        if(parsePararms){
            return;
        }

        QueryStringDecoder decoder = new QueryStringDecoder(msg.uri(),Charset.forName(getCharacterEncoding()));
        paramsMap = decoder.parameters();

        if("application/x-www-form-urlencoded".equalsIgnoreCase(getContentType()) && getInputStream == false){
            //解析post的数据到paramsMap
            if(httpData!=null){
                String data = null;
                try {
                    data = new String(httpData.get());
                    QueryStringDecoder decoder1 = new QueryStringDecoder(data,Charset.forName(getCharacterEncoding()));
                    Map<String,List<String>> paramsMap1 = decoder1.parameters();
                    if(paramsMap1!=null){
                        for(Map.Entry<String,List<String>> kv: paramsMap1.entrySet()){
                            List<String> list = paramsMap.get(kv.getKey());
                            if(list==null){
                                list = kv.getValue();
                            }
                            list.addAll(kv.getValue());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        parsePararms = true;

    }

    /**
     * 设置post提交的数据
     * @param postDatas
     */

    public void setPostDatas(List<InterfaceHttpData> postDatas) {
        this.parts = postDatas;
    }


    public void setHttpData(FileUpload httpData) {
        this.httpData = httpData;
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

    @Override
    public String changeSessionId() {
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

    //先不实现
    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    //先不实现
    @Override
    public void login(String s, String s1) throws ServletException {

    }

    //先不实现
    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {

        if(parts!=null) {
            if(partMap==null) {
                initPartMap();
            }
            return partMap.values();
        }else {
            return null;
        }
    }

    private void initPartMap(){
        partMap = new HashMap<String, Part>((int) (parts.size() / 0.75));
        for (InterfaceHttpData data : parts) {
            partMap.put(data.getName(), new NettyPart(data.retain()));
        }
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        if(parts!=null) {
            if(partMap==null) {
                initPartMap();
            }
            return partMap.get(s);
        }else {
            return null;
        }
    }

    //先不实现
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
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
    public long getContentLengthLong() {
        String length = msg.headers().get(HttpHeaderNames.CONTENT_LENGTH);
        if(length==null){
            return  -1;
        }else{
            return Long.parseLong(length);
        }
    }

    @Override
    public String getContentType() {
        return msg.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        if(getInputStream){
            throw new IllegalStateException("已经获取流，无法获取流");
        }

        if(parsePararms){
            throw new IllegalStateException("已经解析参数，无法获取流");
        }


        String method = getMethod();
        if(!method.equalsIgnoreCase("POST") && method.equalsIgnoreCase("PUT")){
            throw new UnsupportedEncodingException("无法获取输入流");
        }

        if(getContentType().equalsIgnoreCase(HttpRequestHandler.multipart)){
            throw new IllegalStateException("请使用getParts 获取上传的文件");
        }


        if(httpData.isInMemory()){
            getInputStream = true;
            return  new ByteArrayServletInputStream(httpData.get());
        }else{
            //in disk
            getInputStream = true;
            return new FileServletInputStream(httpData.getFile());
        }
    }

    @Override
    public String getParameter(String name) {
        if(!parsePararms){
            parseQueryString();
        }
        List<String> list =  paramsMap.get(name);
        if(list!=null && list.size()>0){
            return list.get(0);
        }else {
            return null;
        }
    }

    @Override
    public Enumeration getParameterNames() {

        if(!parsePararms){
            parseQueryString();
        }

        final Iterator<Map.Entry<String, List<String>>> iterable = paramsMap.entrySet().iterator();
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
    public String[] getParameterValues(String name) {
        if(!parsePararms){
            parseQueryString();
        }
        List<String> list =  paramsMap.get(name);
        return list.toArray(null);
    }

    @Override
    public Map getParameterMap() {
        if(!parsePararms){
            parseQueryString();
        }
        return paramsMap;
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

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    /**
     * 清理性工作
     */
    public void clearRequestData(){

        if(parts!=null)
            for(InterfaceHttpData data:parts){
                while(data.release());
            }

        if(httpData!=null){
            while(httpData.release());
        }

    }

}
