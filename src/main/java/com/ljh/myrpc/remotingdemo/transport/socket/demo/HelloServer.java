package com.ljh.myrpc.remotingdemo.transport.socket.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: ljh
 * DateTime: 2022-03-24 19:58
 */
public class HelloServer {

    private static final Logger logger = LoggerFactory.getLogger(HelloServer.class);

    public void start(int port){
        //1.创建ServerSocket并绑定一个端口
        try (ServerSocket server = new ServerSocket(port)) {
            Socket socket;
            //2.通过accept()方法监听客户端请求
            while((socket = server.accept()) != null){
                logger.info("client connected");
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    //3.通过输入流获取客户端发送的消息
                    Message message = (Message) objectInputStream.readObject();
                    logger.info("server received message: " + message.getContent());
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
                } catch (IOException | ClassNotFoundException e) {
                    logger.info("occur exception: ", e);
                }
            }
        } catch (IOException e) {
            logger.info("occur exception: ", e);
        }
    }
}
