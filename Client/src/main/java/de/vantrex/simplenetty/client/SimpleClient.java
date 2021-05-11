package de.vantrex.simplenetty.client;

import de.vantrex.simplenetty.client.settings.SimpleClientSettings;
import de.vantrex.simplenetty.initializer.SimpleChannelInitializer;
import de.vantrex.simplenetty.packet.SimplePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.SocketAddress;

public class SimpleClient {

    private static final boolean EPOLL = Epoll.isAvailable();

    private final SimpleClientSettings settings;
    private EventLoopGroup worker;

    private Channel channel;
    private Bootstrap bootstrap;


    public SimpleClient(SimpleClientSettings settings) {
        this.settings = settings;
        new Thread(this::init).start();
    }

    private void init() {
        worker = EPOLL ? new EpollEventLoopGroup(settings.threads()) : new NioEventLoopGroup(settings.threads());
        try {
            bootstrap = new Bootstrap()
                    .group(worker)
                    .channel(EPOLL ? EpollSocketChannel.class : NioServerSocketChannel.class)
                    .handler(new SimpleChannelInitializer<SocketChannel>(settings.protocol(), false));

                    channel = bootstrap.connect(settings.address(), settings.port())
                    .sync()
                    .channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (channel != null)
            throw new IllegalStateException("Client is already connected!");
        bootstrap.handler(new SimpleChannelInitializer<SocketChannel>(settings.protocol(), false));
        try {
            bootstrap.connect().sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        checkChannel();
        settings.protocol().close();
        worker.shutdownGracefully();
        settings.protocol().setClientSession(null);
    }

    public SocketAddress getLocalAddress() {
        checkChannel();
        return channel.localAddress();
    }

    public SocketAddress getRemoteAddress() {
        checkChannel();
        return channel.remoteAddress();
    }

    public EventLoopGroup getWorker() {
        return worker;
    }

    public Channel getChannel() {
        return channel;
    }

    public void send(SimplePacket packet) {
        checkChannel();
        settings.protocol().getClientSession().send(packet);
    }

    private void checkChannel() {
        if (channel == null) {
            throw new IllegalStateException("Client Channel is not connected!");
        }
    }

}
