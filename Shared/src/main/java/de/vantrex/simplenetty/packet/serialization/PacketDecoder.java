package de.vantrex.simplenetty.packet.serialization;

import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.packet.exceptions.PacketNotFoundException;
import de.vantrex.simplenetty.protocol.Protocol;
import de.vantrex.simplenetty.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class PacketDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Protocol<?> protocol;

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buf, List<Object> list) {
        int length = buf.readInt();
        if (length > 0) {
            SimplePacket packet = protocol.readIdentifierFromByteBuf(buf);
            if (packet == null) {
                throw new PacketNotFoundException("Packet not found (Identifier: " + protocol.getIdentifier().getSimpleName() + ") not found.");
            }
            packet.read(buf);
            list.add(packet);
        }
    }
}