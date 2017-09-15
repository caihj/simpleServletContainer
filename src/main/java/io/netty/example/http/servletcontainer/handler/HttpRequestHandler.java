package io.netty.example.http.servletcontainer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.servletcontainer.container.SimpleContainer;
import io.netty.example.http.servletcontainer.net.NettyHttpServletRequest;
import io.netty.example.http.servletcontainer.net.NettyHttpServletResponse;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.List;

public class HttpRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    private SimpleContainer container;

    private HttpPostRequestDecoder decoder;

    private List<InterfaceHttpData> postDatas;

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

            try {
                decoder = new HttpPostRequestDecoder(factory, request);
            } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                e1.printStackTrace();
                ctx.channel().close();
                return;
            }

            if (decoder != null) {
                if (msg instanceof HttpContent) {
                    // New chunk is received
                    HttpContent chunk = (HttpContent) msg;
                    try {
                        decoder.offer(chunk);
                    } catch (HttpPostRequestDecoder.ErrorDataDecoderException e1) {
                        e1.printStackTrace();
                        ctx.channel().close();
                        return;
                    }

                    // example of reading chunk by chunk (minimize memory usage due to
                    // Factory)
                    readHttpData();
                    // example of reading only if at the end
                    if (chunk instanceof LastHttpContent) {
                        nettyRequest.setPostDatas(postDatas);
                        container.onRequest(nettyRequest,nettyResponse);
                        nettyResponse.flushBuffer();
                    }
                }
            } else {

                ctx.channel().close();
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

}
