package com.ljh.myrpc.remotingdemo.transport.netty.client;

import com.ljh.myrpc.remotingdemo.dto.RpcRequest;
import com.ljh.myrpc.remotingdemo.dto.RpcResponse;
import com.ljh.myrpc.remotingdemo.serialize.KryoSerializer;
import com.ljh.myrpc.remotingdemo.transport.netty.codec.NettyKryoDecoder;
import com.ljh.myrpc.remotingdemo.transport.netty.codec.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;

import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: ljh
 * DateTime: 2022-04-09 18:24
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    //初始化相关资源，例如EventLoopGroup，Bootstrap
    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                //连接的超时时间，超过这个时间还是建立不上连接的话则代表连接失败
                //如果15秒之内没有发送数据给服务器，则发送一次心跳请求
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch){
                        /*
                        自定义序列化编码器
                         */
                        //RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        //ByteBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     * 1.首先初始化了一个Bootstrap
     * 2.通过Bootstrap对象连接到服务端
     * 3.通过Channel向服务端发送消息RpcRequest
     * 4.发送成功后，阻塞等待，直到Channel关闭
     * 5.拿到服务端返回的结果RpcResponse
     * @param rpcRequest
     * @return 服务端返回的数据
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest){
        try{
            ChannelFuture f = b.connect(host, port).sync();
            logger.info("client connect {}", host + ":" + port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if(futureChannel != null){
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if(future.isSuccess()){
                        logger.info("client send message: [{}]", rpcRequest.toString());
                    } else {
                        logger.error("send failed:", future.cause());
                    }
                });
                //阻塞等待，直到Channel关闭
                futureChannel.closeFuture().sync();
                //将服务端返回的数据也就是RpcResponse对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("occur exception when connect server", e);
        }
        return null;
    }
}
