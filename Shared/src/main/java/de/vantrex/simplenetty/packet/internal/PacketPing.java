package de.vantrex.simplenetty.packet.internal;

import de.vantrex.simplenetty.annotations.PacketId;
import de.vantrex.simplenetty.packet.SimplePacket;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@PacketId(id = -99)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketPing extends SimplePacket {

    private long time;

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(time);
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.time = byteBuf.readLong();
    }
}
