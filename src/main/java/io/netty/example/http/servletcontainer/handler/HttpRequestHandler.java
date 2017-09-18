package io.netty.example.http.servletcontainer.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.servletcontainer.container.SimpleContainer;
import io.netty.example.http.servletcontainer.net.NettyHttpServletRequest;
import io.netty.example.http.servletcontainer.net.NettyHttpServletResponse;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    private SimpleContainer container;

    /**
     * 解析  multipart/form-data，application/x-www-form-urlencoded
     */
    private HttpPostRequestDecoder decoder;

    private List<InterfaceHttpData> postDatas = new ArrayList<InterfaceHttpData>();

    /**
     * 保存除文件上传和表单之外的其他数据
     */
    private FileUpload httpData;

    String content_type ;

    public static final String multipart="multipart/form-data";

    private final int max_in_memory_content = 1024*1024;

    private static final HttpDataFactory factory =
            new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    NettyHttpServletRequest nettyRequest;
    NettyHttpServletResponse nettyResponse;

    public HttpRequestHandler(SimpleContainer container){
        this.container = container;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
             nettyRequest = new NettyHttpServletRequest(ctx, (HttpRequest) request);
             nettyResponse = new NettyHttpServletResponse(ctx);

            if(request.method().equals(HttpMethod.GET)){
                container.onRequest(nettyRequest,nettyResponse);
                nettyResponse.flushBuffer();
                return;
            }

            //post
            content_type = nettyRequest.getContentType();

            if( multipart.equals(content_type)){
                try {
                    decoder = new HttpPostRequestDecoder(factory, request);
                } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    ctx.channel().close();
                    return;
                }
            }else{
                int content_length = ((HttpRequest) msg).headers().getInt(HttpHeaderNames.CONTENT_LENGTH);
                if( content_length > max_in_memory_content ){
                    httpData = new MemoryFileUpload("body","body",content_type,null,null,content_length);
                }else{
                    httpData = new DiskFileUpload("body","body",content_type,null,null,content_length);
                }
            }
        }

        //是否支持异步
        boolean isAsync = false;

        if(msg instanceof HttpContent){
            if(decoder!=null){
                // New chunk is received
                HttpContent chunk = (HttpContent) msg;
                try {
                    decoder.offer(chunk);
                } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    ctx.channel().close();
                    return;
                }
                readHttpData();
                if (chunk instanceof LastHttpContent) {
                    nettyRequest.setPostDatas(postDatas);
                    container.onRequest(nettyRequest,nettyResponse);
                    if(!isAsync){
                        nettyResponse.flushBuffer();
                        nettyRequest.clearRequestData();
                    }
                }
            }else{
                //保存post的数据到内存或者文件
                boolean isLast = msg instanceof LastHttpContent;
                fileHttpData((HttpContent) msg,isLast);

                if (isLast) {
                    nettyRequest.setPostDatas(postDatas);
                    nettyRequest.setHttpData(httpData.retain());
                    container.onRequest(nettyRequest,nettyResponse);
                    if(!isAsync){
                        nettyResponse.flushBuffer();
                        nettyRequest.clearRequestData();
                    }
                }
            }
        }
    }

    public void readHttpData(){
        while (decoder.hasNext()) {
            InterfaceHttpData data = decoder.next();
            if (data != null) {
                postDatas.add(data);
            }
        }
    }
    public void fileHttpData(HttpContent content,boolean last){
        try {
            httpData.addContent(content.content().retain(),last);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
