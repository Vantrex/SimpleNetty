package de.vantrex.simplenetty.protocol;

import de.vantrex.simplenetty.annotations.exceptions.AnnotationNotFoundException;
import de.vantrex.simplenetty.listener.SimplePacketListener;
import de.vantrex.simplenetty.listener.SimpleSessionListener;
import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.packet.exceptions.PacketAlreadyRegisteredException;
import de.vantrex.simplenetty.packet.exceptions.PacketIdAlreadyRegisteredException;
import de.vantrex.simplenetty.session.Session;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Protocol<T> {

    Class<T> getIdentifier();

    SimplePacket createPacket(T t);

    T getPacketIdentifier(SimplePacket packet);

    T getPacketIdentifier(Class<? extends SimplePacket> packet);

    void registerPacket(Class<? extends SimplePacket> packet) throws AnnotationNotFoundException, PacketAlreadyRegisteredException, PacketIdAlreadyRegisteredException;

    void registerListener(SimplePacketListener listener);

    void callPacketHandler(SimplePacket packet, Session session);

    void registerPackets(Collection<Class<? extends SimplePacket>> classes) throws AnnotationNotFoundException, PacketAlreadyRegisteredException, PacketIdAlreadyRegisteredException;

    void addSession(Session session);

    void removeSession(Session session);

    void setClientSession(Session clientSession);

    Session getClientSession();

    List<Session> getSessions();

    void close();

    void writeIdentifierToByteBuf(ByteBuf byteBuf, SimplePacket packet);

    SimplePacket readIdentifierFromByteBuf(ByteBuf byteBuf);

    void registerSessionListener(SimpleSessionListener listener);

    void unregisterSessionListener(SimpleSessionListener listener);

    List<SimpleSessionListener> getSessionListeners();

    Map<SimplePacketListener, Map<Class<? extends SimplePacket>, Method>> getPacketListeners();

}
