package com.github.mushanwb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// 服务端
public class Server {

    // 需要一个 ServerSocket 服务
    private final ServerSocket server;

    /**
     * @param port TCP 连接的端口号，0 ~ 65535
     * @throws IOException IO 异常
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public void start() throws IOException {
        // 创建一个死循环，服务端需要一直监听
        while (true) {
            // 监听一个端口，如果没有客户端连接的话，将一直阻塞，
            // 当有客户端连接的时候，立刻返回一个 socket，可以收发数据
            Socket socket = server.accept();
        }
    }

    public static void main(String[] args) {

    }
}
