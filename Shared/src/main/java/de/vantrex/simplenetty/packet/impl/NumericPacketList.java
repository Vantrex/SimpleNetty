package de.vantrex.simplenetty.packet.impl;

import de.vantrex.simplenetty.packet.PacketList;
import de.vantrex.simplenetty.packet.SimplePacket;

import java.util.HashMap;
import java.util.Map;

public class NumericPacketList implements PacketList<Integer> {

    private final Map<Integer, Class<? extends SimplePacket>> packets = new HashMap<>();

    @Override
    public Class<? extends SimplePacket> getPacket(Integer integer) {
        return packets.get(integer);
    }

    @Override
    public Integer getIdentifierByPacket(Class<? extends SimplePacket> packet) {
        for (Map.Entry<Integer, Class<? extends SimplePacket>> entry : this.packets.entrySet()) {
            if (entry.getValue() == packet)
                return entry.getKey();
        }

        return null;
    }

    @Override
    public void addPacket(Integer integer, Class<? extends SimplePacket> packet) {
        this.packets.put(integer, packet);
    }

    @Override
    public void removePacket(Integer integer) {
        this.packets.remove(integer);
    }

    @Override
    public void removePacket(Class<? extends SimplePacket> packet) {
        Integer identifier = getIdentifierByPacket(packet);
        if (identifier != null)
            packets.remove(identifier);
    }

    @Override
    public Map<Integer, Class<? extends SimplePacket>> getPackets() {
        return packets;
    }
}