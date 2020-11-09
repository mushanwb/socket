package com.github.mushanwb;

import java.net.Socket;

// 当一个客户端连上服务端 用多线程的方式读写数据
public class ClientConnection extends Thread {

    private Socket socket;

    /**
     * @param socket 接收一个 socket 数据
     */
    public ClientConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
    }
}
