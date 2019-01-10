package com.linghong.fkdp.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Auther: luck_nhb
 * @Date: 2019/1/6 12:43
 * @Version 1.0
 * @Description:  netty启动器
 */
@Component
public class ImServer  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture future;
    private static ImServer imServer = new ImServer();

    private ImServer(){
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap()
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ImServerInitializer());
    }

    public static ImServer getInstance(){
       return imServer;
    }

    public void start(int port){
         this.future = serverBootstrap.bind(port);
         logger.info("imServer启动");
    }
}
