package de.vantrex.simplenetty.packet.serialization;

import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PacketEncoder extends MessageToMessageEncoder<SimplePacket> {

    private final Protocol<?> protocol;

    @Override
    protected void encode(ChannelHandlerContext context, SimplePacket packet, List<Object> out) throws Exception {
        ByteBuf buf = context.alloc().buffer();
        protocol.writeIdentifierToByteBuf(buf, packet);
        packet.write(buf);
        out.add(buf);
    }
}
