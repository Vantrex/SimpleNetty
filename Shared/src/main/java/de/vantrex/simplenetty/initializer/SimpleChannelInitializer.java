package de.vantrex.simplenetty.initializer;

import de.vantrex.simplenetty.packet.serialization.PacketDecoder;
import de.vantrex.simplenetty.packet.serialization.PacketEncoder;
import de.vantrex.simplenetty.protocol.Protocol;
import de.vantrex.simplenetty.session.Session;
import de.vantrex.simplenetty.session.SimpleSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class SimpleChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    private final Protocol<?> protocol;
    private final boolean isServer;

    public SimpleChannelInitializer(Protocol<?> protocol, boolean isServer) {
        this.protocol = protocol;
        this.isServer = isServer;
    }

    @Override
    protected void initChannel(C channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4));
        pipeline.addLast(new PacketDecoder(protocol));

        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new PacketEncoder(protocol));

        SimpleSession session = new SimpleSession(channel, protocol);
        pipeline.addLast(session);

        if (isServer) {
            protocol.addSession(session);
        } else {
            protocol.setClientSession(session);
        }


    }
}
