package de.vantrex.simplenetty.server;

import de.vantrex.simplenetty.initializer.SimpleChannelInitializer;
import de.vantrex.simplenetty.packet.SimplePacket;
import de.vantrex.simplenetty.protocol.Protocol;
import de.vantrex.simplenetty.server.settings.SimpleServerSettings;
import de.vantrex.simplenetty.session.Session;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Getter;

import java.util.concurrent.locks.AbstractOwnableSynchronizer;

@Getter
public class SimpleServer {

    private final static boolean EPOLL = Epoll.isAvailable();


    private final SimpleServerSettings settings;

    private Channel channel;

    public SimpleServer(SimpleServerSettings settings) {
        this.settings = settings;
        new Thread(this::init).start();
    }

    private void init() {
        EventLoopGroup boss = EPOLL ? new EpollEventLoopGroup(settings.bossThreads()) : new NioEventLoopGroup(settings.bossThreads());
        EventLoopGroup worker = EPOLL ? new EpollEventLoopGroup(settings.workerThreads()) : new NioEventLoopGroup(settings.workerThreads());

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new SimpleChannelInitializer<SocketChannel>(settings.protocol(), true));
            if (settings.logger())
                bootstrap = bootstrap.handler(new LoggingHandler(LogLevel.INFO));

            channel = bootstrap.bind(settings.port())
                    .sync()
                    .channel();
            channel.closeFuture().syncUninterruptibly();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public void close() {
        channel.close();
    }

    public void send(SimplePacket packet) {
        for (Session session : settings.protocol().getSessions()) {
            session.send(packet);
        }
    }

}