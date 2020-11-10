package com.github.mushanwb;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

// 当一个客户端连上服务端 用多线程的方式读写数据
public class ClientConnection extends Thread {

    private Socket socket;

    // 每当有一个客户端连上的时候，
    // 需要有一个 客户端昵称 和 id，来标识是哪一个客户端（重点是 id）
    private String clientName;
    private Integer clientId;
    private Server server;

    public String getClientName() {
        return clientName;
    }

    public Integer getClientId() {
        return clientId;
    }

    /**
     * @param socket 接收一个 socket 数据
     */
    public ClientConnection(Socket socket,
                            Server server,
                            Integer clientId) {
        this.clientId = clientId;
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // 从 socket 数据中读取我们需要的数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            // 循环读取流中的数据
            while ((line = reader.readLine()) != null) {
                // 第一次连上服务端的时候，将昵称发送给服务端
                if (clientName == null) {
                    clientName = line;
                    // 注册用户
                    server.registerClient(this);
                } else {
                    // 不是第一次连接的时候，将获取客户端发送的数据，并且由服务器转发给另外一个客户端
                    Message message = JSON.parseObject(line, Message.class);
                    server.sendMessage(this, message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 下线
            server.clientOffline(this);
        }
    }

    /**
     * @param message 发送的消息
     * @throws IOException  IO 异常
     */
    public void sendMessage(String message) throws IOException {
        socket.getOutputStream().write(message.getBytes());
        socket.getOutputStream().write('\n');
        socket.getOutputStream().flush();
    }
}
