package io.netty.example.http.servletcontainer.net;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;

public class NettyHttpServletResponse implements HttpServletResponse {

    private String charset="UTF-8";

    private ChannelHandlerContext ctx;

    private FullHttpResponse response;

    private ByteBuf buf;

    public NettyHttpServletResponse(ChannelHandlerContext ctx){
        this.ctx = ctx;
        response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        buf = ctx.alloc().buffer(8192);
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
         return response.headers().contains(name);
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

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
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
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
                buf.writeByte(b);
            }

            @Override
            public void flush() throws IOException {
                ctx.flush();
            }

            @Override
            public void close() throws IOException {
                ctx.flush();
                ctx.close();
            }
        };


        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {


        Writer writer = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                buf.writeCharSequence(new CharArray(cbuf,off,len,false), Charset.forName(charset));
            }

            @Override
            public void flush() throws IOException {
                ctx.flush();
            }

            @Override
            public void close() throws IOException {
                ctx.flush();
                ctx.close();
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

    }

    @Override
    public void setContentType(String type) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, type);
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        setContentLength(buf.readableBytes());
        response = response.replace(buf);
        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
