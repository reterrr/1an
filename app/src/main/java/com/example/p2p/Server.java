package com.example.p2p;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public final class Server {
    private final int port;
    private RouterHandler routerHandler;
    private final PortListener listener;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture future;

    public Server(int port, PortListener portListener) {
        this.port = port;
        this.listener = portListener;
        this.routerHandler = new RouterHandler();
    }

    public interface PortListener {
        void onPortReady(int port);
    }

    public void configMapping(Consumer<RouterHandler> configurator) {
        configurator.accept(routerHandler);
    }

    public int getPort() {
        return ((InetSocketAddress) future.channel().localAddress()).getPort();
    }

    public void run() throws SSLException, InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(
//                                    new LengthFieldBasedFrameDecoder(
//                                            10 * 1024 * 1024, 0, 4, 0, 4),
                                    new RequestDecoder(),
                                    routerHandler,
                                    new LengthFieldPrepender(4)
                            );
                        }
                    });

            future = b.bind(port).sync();
            listener.onPortReady(getPort());
            future.channel().closeFuture().sync();
        } finally {
            shutdownEventLoops();
        }
    }

    /**
     * Signal the server to stop accepting new connections,
     * close existing channel, and tear down //
     * event loops.
     */
    public void shutdown() {
        if (future != null) {
            future.channel().close();
        }
        shutdownEventLoops();
        routerHandler = null;
    }

    private void shutdownEventLoops() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
    }
}

