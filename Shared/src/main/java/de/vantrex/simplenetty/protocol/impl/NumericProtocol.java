package de.vantrex.simplenetty.protocol.impl;

import de.vantrex.simplenetty.annotations.PacketId;
import de.vantrex.simplenetty.annotations.exceptions.AnnotationNotFoundException;
import de.vantrex.simplenetty.listener.SimplePacketListener;
import de.vantrex.simplenetty.listener.SimpleSessionListener;
import de.vantrex.simplenetty.listener.handler.SimplePacketHandler;
import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.packet.exceptions.PacketAlreadyRegisteredException;
import de.vantrex.simplenetty.packet.exceptions.PacketIdAlreadyRegisteredException;
import de.vantrex.simplenetty.packet.impl.NumericPacketList;
import de.vantrex.simplenetty.protocol.Protocol;
import de.vantrex.simplenetty.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class NumericProtocol implements Protocol<Integer> {

    private final Map<SimplePacketListener, Map<Class<? extends SimplePacket>, Method>> listeners = new HashMap<>();
    private final List<SimpleSessionListener> sessionListeners = new ArrayList<>();
    private final NumericPacketList packetList = new NumericPacketList();

    private Session clientSession;
    private final List<Session> sessions = new ArrayList<>();


    @Override
    public Class<Integer> getIdentifier() {
        return Integer.class;
    }

    @Override
    public SimplePacket createPacket(Integer integer) {
        try {
            Class<? extends SimplePacket> packetClass = packetList.getPacket(integer);
            if (packetClass == null)
                return null;
            return packetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) { }
        return null;
    }

    @Override
    public Integer getPacketIdentifier(SimplePacket packet) {
        return getPacketIdentifier(packet.getClass());
    }

    @Override
    public Integer getPacketIdentifier(Class<? extends SimplePacket> packet) {
        return packetList.getIdentifierByPacket(packet);
    }

    @Override
    public void registerPacket(Class<? extends SimplePacket> packet) throws AnnotationNotFoundException, PacketAlreadyRegisteredException, PacketIdAlreadyRegisteredException {
        PacketId id = packet.getAnnotation(PacketId.class);
        if (id == null)
            throw new AnnotationNotFoundException("Annotation PacketId not found in class " + packet.getName());
        if (packetList.getPacket(id.id()) != null)
            throw new PacketIdAlreadyRegisteredException("There is already a packet registered with id " + id.id());
        if (packetList.getIdentifierByPacket(packet) != null)
            throw new PacketAlreadyRegisteredException("Packet " + packet.getName() + " is already registered.");
        packetList.addPacket(id.id(), packet);
    }

    @Override
    public void registerListener(SimplePacketListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.getAnnotation(SimplePacketHandler.class) != null) {
                if (method.getParameterCount() == 1) {
                    Class<?> clazz = method.getParameterTypes()[0];
                    if (SimplePacket.class.isAssignableFrom(clazz)) {
                        if (!listeners.containsKey(listener))
                            listeners.put(listener, new HashMap<>());
                        listeners.get(listener).put((Class<? extends SimplePacket>) clazz, method);
                    }
                    continue;
                }
                if (method.getParameterCount() == 2) {
                    Class<?> clazz = method.getParameterTypes()[0];
                    Class<?> clazz2 = method.getParameterTypes()[1];
                    if (SimplePacket.class.isAssignableFrom(clazz) && clazz2 == Session.class) {
                        if (!listeners.containsKey(listener))
                            listeners.put(listener, new HashMap<>());
                        listeners.get(listener).put((Class<? extends SimplePacket>) clazz, method);
                    }
                }
            }
        }
    }

    @Override
    public void callPacketHandler(SimplePacket packet, Session session) {

        for (Map.Entry<SimplePacketListener, Map<Class<? extends SimplePacket>, Method>> entry : listeners.entrySet()) {
            if (entry.getValue().containsKey(packet.getClass())) {
                try {
                    Method m = entry.getValue().get(packet.getClass());
                    if (m.getParameterCount() == 1)
                        m.invoke(entry.getKey(), packet);
                    else
                        m.invoke(entry.getKey(), packet, session);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void registerPackets(Collection<Class<? extends SimplePacket>> classes) throws AnnotationNotFoundException, PacketAlreadyRegisteredException, PacketIdAlreadyRegisteredException {

        for (Class<? extends SimplePacket> clazz : classes) {
            if (clazz.getSuperclass() == SimplePacket.class)
                registerPacket(clazz);
        }
    }

    @Override
    public void addSession(Session session) {
        sessions.add(session);
    }

    @Override
    public void removeSession(Session session) {
        sessions.remove(session);
    }

    @Override
    public void setClientSession(Session clientSession) {
        this.clientSession = clientSession;
    }

    @Override
    public Session getClientSession() {
        return clientSession;
    }

    @Override
    public List<Session> getSessions() {
        return sessions;
    }

    @Override
    public void close() {
        if (this.clientSession != null && this.clientSession.getChannel().isOpen() && this.clientSession.getChannel().isOpen()) {
            this.clientSession.close();
        }
        if (!this.sessions.isEmpty()) {
            this.sessions.forEach(Session::close);
        }
    }

    @Override
    public void writeIdentifierToByteBuf(ByteBuf byteBuf, SimplePacket packet) {
        byteBuf.writeInt(getPacketIdentifier(packet));
    }

    @Override
    public SimplePacket readIdentifierFromByteBuf(ByteBuf byteBuf) {
        return createPacket(byteBuf.readInt());
    }

    @Override
    public void registerSessionListener(SimpleSessionListener listener) {
        this.sessionListeners.add(listener);
    }

    @Override
    public void unregisterSessionListener(SimpleSessionListener listener) {
        this.sessionListeners.remove(listener);
    }

    @Override
    public List<SimpleSessionListener> getSessionListeners() {
        return sessionListeners;
    }


}
