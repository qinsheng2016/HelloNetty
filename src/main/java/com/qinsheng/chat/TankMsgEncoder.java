package com.qinsheng.chat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class TankMsgEncoder extends MessageToByteEncoder<TankMsg> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TankMsg msg, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(msg.x);
        byteBuf.writeInt(msg.y);
    }
}
