package de.vantrex.simplenetty.session;

import de.vantrex.simplenetty.packet.SimplePacket;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.List;

public interface Session {

    void send(SimplePacket packet);

    Channel getChannel();

    SocketAddress getAddress();

    void close();

    void ping();


}
