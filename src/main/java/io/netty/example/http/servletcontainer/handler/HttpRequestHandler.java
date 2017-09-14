package io.netty.example.http.servletcontainer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.http.servletcontainer.container.SimpleContainer;
import io.netty.example.http.servletcontainer.net.NettyHttpServletRequest;
import io.netty.example.http.servletcontainer.net.NettyHttpServletResponse;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private SimpleContainer container;

    public HttpRequestHandler(SimpleContainer container){
        this.container = container;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        NettyHttpServletRequest request = new NettyHttpServletRequest(ctx,msg);
        NettyHttpServletResponse response = new NettyHttpServletResponse(ctx);
        container.onRequest(request,response);
        response.flushBuffer();
    }

}
