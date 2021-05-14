package de.vantrex.simplenetty.session;

import de.vantrex.simplenetty.listener.SimpleSessionListener;
import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.packet.internal.PacketPing;
import de.vantrex.simplenetty.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

public class SimpleSession extends SimpleChannelInboundHandler<SimplePacket> implements Session {

    private Channel channel;
    private Protocol<?> protocol;

    public SimpleSession(Channel channel, Protocol<?> protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    @Override
    public void send(SimplePacket packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public SocketAddress getAddress() {
        return channel.remoteAddress() == null ? channel.localAddress() : channel.remoteAddress();
    }

    @Override
    public void close() {
        if (this.channel != null && this.channel.isOpen())
            this.channel.close();
    }

    @Override
    public void ping() {
        send(new PacketPing(System.currentTimeMillis()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, SimplePacket packet) throws Exception {
        protocol.callPacketHandler(packet, this);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        protocol.getSessionListeners().forEach(listener -> listener.onConnect(this));
    }


    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        protocol.getSessionListeners().forEach(listener -> listener.onDisconnect(this));
        protocol.removeSession(this);
    }

}
