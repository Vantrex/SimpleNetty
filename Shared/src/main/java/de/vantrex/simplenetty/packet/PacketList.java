package de.vantrex.simplenetty.packet;


import java.util.Map;

public interface PacketList<T> {

    Class<? extends SimplePacket> getPacket(T t);

     T getIdentifierByPacket(Class<? extends SimplePacket> packet);

    void addPacket(T t, Class<? extends SimplePacket> packet);

    void removePacket(T t);

    void removePacket(Class<? extends SimplePacket>  packet);

    Map<T, Class<? extends SimplePacket>> getPackets();

}
