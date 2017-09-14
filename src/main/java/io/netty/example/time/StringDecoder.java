package io.netty.example.time;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class StringDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println(channelHandlerContext.hashCode()+" Decoder");
        byte [] buf= new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buf);
        list.add(new String(buf));
    }
}
