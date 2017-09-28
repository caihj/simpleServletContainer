package io.netty.example.http.servletcontainer.net;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.http.servletcontainer.util.GenTools;
import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.concurrent.GenericFutureListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

public class NettyHttpServletResponse implements HttpServletResponse {

    private String charset="UTF-8";

    private ChannelHandlerContext ctx;

    private FullHttpResponse response;

    private ByteBuf buf;

    private boolean isCommit = false;

    private ServletContext servletContext;

    private List<Cookie> cookies = new ArrayList<Cookie>();

    /**
     * 是否启用了异步
     */
    private boolean isAsync = false;

    /**
     *
     *当servlet处理完后，关闭连接
     */
    private boolean closeConnection = false;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public NettyHttpServletResponse(ChannelHandlerContext ctx){
        this.ctx = ctx;
        response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        buf = ctx.alloc().buffer(8192);
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
         return response.headers().contains(name);
    }

    @Override
    public String encodeURL(String url) {
        return url;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    /**
     * not support msg
     * @param sc
     * @param msg
     * @throws IOException
     */
    @Override
    public void sendError(int sc, String msg) throws IOException {
        if(isCommit)
            throw new IllegalStateException("已提交");

        response.setStatus(HttpResponseStatus.valueOf(sc));
        buf.clear();
        flushBuffer();
    }

    @Override
    public void sendError(int sc) throws IOException {
        if(isCommit)
            throw new IllegalStateException("已提交");

        response.setStatus(HttpResponseStatus.valueOf(sc));
        buf.clear();
        flushBuffer();
    }

    @Override
    public void sendRedirect(String location) throws IOException {

        if(isCommit)
            throw new IllegalStateException("已提交");

        response.setStatus(HttpResponseStatus.FOUND);
        response.headers().add(HttpHeaderNames.LOCATION,encodeRedirectURL(getRedirectUrl(location)));
        buf.clear();
        flushBuffer();
    }

    private  String getRedirectUrl(String location){

        if(location.startsWith("//")){
            return "http:"+location;
        }else if(location.startsWith("/")){
            return location;
        }else{
            return servletContext.getContextPath()+"/"+location;
        }
    }

    @Override
    public void setDateHeader(String name, long date) {
        CharSequenceValueConverter charSequenceValueConverter = new CharSequenceValueConverter();
        response.headers().set(name,charSequenceValueConverter.convertTimeMillis(date));
    }

    @Override
    public void addDateHeader(String name, long date) {
        CharSequenceValueConverter charSequenceValueConverter = new CharSequenceValueConverter();
        response.headers().add(name,charSequenceValueConverter.convertTimeMillis(date));
    }

    @Override
    public void setHeader(String name, String value) {
        response.headers().set(name,value);
    }

    @Override
    public void addHeader(String name, String value) {
        response.headers().add(name,value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        response.headers().setInt(name,value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        response.headers().addInt(name,value);
    }

    @Override
    public void setStatus(int sc) {
        response.setStatus(HttpResponseStatus.valueOf(sc));
    }

    @Override
    public void setStatus(int sc, String sm) {
        response.setStatus(new HttpResponseStatus(sc,sm));
    }

    @Override
    public int getStatus() {
        return response.status().code();
    }

    @Override
    public String getHeader(String s) {
        return response.headers().get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        List<String> value = response.headers().getAll(s);
        if(value!=null) {
            return value;
        }
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return response.headers().names();
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public String getContentType() {
        return response.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {

        ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return ctx.channel().isWritable();
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) throws IOException {
                if(buf.capacity()-buf.writerIndex() >= 1) {
                    buf.writeByte(b);
                }else{
                    flushBuffer();
                    buf.ensureWritable(1);
                    buf.writeByte(b);
                }
            }

            @Override
            public void flush() throws IOException {
                ctx.write(buf);
                ctx.flush();
            }

            @Override
            public void close() throws IOException {
                closeConnection = true;
                flushBuffer();
            }
        };

        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        Writer writer = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                if(buf.capacity()-buf.writerIndex() >= len) {
                    buf.writeCharSequence(new CharArray(cbuf, off, len, false), Charset.forName(charset));
                }else{
                    flushBuffer();
                    buf.ensureWritable(len);
                    buf.writeCharSequence(new CharArray(cbuf,off,len,false), Charset.forName(charset));
                }
            }

            @Override
            public void flush() throws IOException {
                ctx.write(buf);
                ctx.flush();
            }

            @Override
            public void close() throws IOException {
                closeConnection = true;
                flushBuffer();
            }

        };

        PrintWriter printWriter = new PrintWriter(writer);
        return printWriter;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setContentLength(int len) {
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, len);
    }

    @Override
    public void setContentLengthLong(long l) {
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(l));
    }

    @Override
    public void setContentType(String type) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, type);
    }

    @Override
    public void setBufferSize(int size) {
        if(isCommit)
            throw  new IllegalStateException("缓存已提交");
        buf.capacity(size);
    }

    @Override
    public int getBufferSize() {
        return buf.capacity();
    }

    /**
     * 当发送数据完毕，关闭连接
     */
    public void addCloseOnComplement(){
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void flushBuffer() throws IOException {

        ChannelFuture future = null;

        if(isCommit){
            future = ctx.write(buf);
            ctx.flush();
        }else{
            response = response.replace(buf);

            //content-length和chunk都没有，则设置缓存区的长度为 content-length
            if(response.headers().get(HttpHeaderNames.CONTENT_LENGTH)==null && response.headers().get(HttpHeaderNames.TRANSFER_ENCODING)==null) {
                //没有设置content-length ,设置发送完成后连接关闭
                response.headers().set(HttpHeaderNames.TRANSFER_ENCODING, "chunked");
                response.headers().set(HttpHeaderNames.CONNECTION,"close");
                if(!isAsync)
                    closeConnection = true;
            }

            if(cookies.size()>0){
                List<io.netty.handler.codec.http.cookie.Cookie> nettyCookies = new ArrayList<io.netty.handler.codec.http.cookie.Cookie>(cookies.size());
                for(Cookie cookie:cookies){
                    io.netty.handler.codec.http.cookie.Cookie cookie1 = new DefaultCookie (cookie.getName(),cookie.getValue());
                    cookie1.setDomain(cookie.getDomain());
                    cookie1.setHttpOnly(cookie.isHttpOnly());
                    cookie1.setMaxAge(cookie.getMaxAge());
                    cookie1.setPath(cookie.getPath());
                    cookie1.setSecure(cookie.getSecure());
                    nettyCookies.add(cookie1);
                }
                List<String> cookieStr = ServerCookieEncoder.STRICT.encode(nettyCookies);
                for(String str:cookieStr){
                    response.headers().add(HttpHeaderNames.SET_COOKIE,str);
                }
            }
            future = ctx.write(response);
            ctx.flush();
            isCommit = true;
        }

    }

    @Override
    public void resetBuffer() {
        if(isCommit){
           throw new IllegalStateException("已提交");
        }else{
            buf.clear();
        }
    }

    @Override
    public boolean isCommitted() {
        return isCommit;
    }

    @Override
    public void reset() {
        if(isCommit){
            throw new IllegalStateException("已提交");
        }else{
            //清空buf和头信息
            buf.clear();
            response.headers().clear();
        }

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public boolean isCloseConnection() {
        return closeConnection;
    }

    public void setCloseConnection(boolean closeConnection) {
        this.closeConnection = closeConnection;
    }
}
