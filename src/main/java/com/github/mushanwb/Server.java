package com.github.mushanwb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// 服务端
public class Server {

    // 需要一个 ServerSocket 服务
    private final ServerSocket server;
    private final Map<Integer, ClientConnection> clients = new ConcurrentHashMap<>();
    private static AtomicInteger COUNTER = new AtomicInteger(0);

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

            // 使用多线程的方式来读取客户端发送的数据
            // 如果不用多线程方式读取的话，当服务端正在读取一个用户的信息时，accept 方法不执行
            // 这样会导致其他用户将连接不进来，因此我们需要将读取信息的操作交给多线程处理
            new ClientConnection(socket, this, COUNTER.incrementAndGet()).start();
        }
    }

    public static void main(String[] args) throws IOException {
        // 使用 8081 端口，并启动死循环监听
        new Server(8080).start();
    }

    /**
     * 注册用户
     * 每次有客户端连接上来的时候，将客户端的 id 自增 1，客户端 id 从 1 开始
     * 当服务端发送消息选择的客户端 id 为 0 时，则代表给所有人发送消息，否则给某一个指定客户端发消息
     * @param clientConnection 连接的客户端
     */
    public void registerClient(ClientConnection clientConnection) {
        clients.put(clientConnection.getClientId(), clientConnection);
        // 通告用户上线了
        clientOnline(clientConnection);
    }

    /**
     * 用户上线，通知所有的人
     * @param clientConnection  上线用户
     */
    private void clientOnline(ClientConnection clientConnection) {
        clients.values().forEach(client -> dispatchMessage(client, "系统", "所有人", clientConnection.getClientName() + "上线了" + getAllClientsInfo()));
    }

    /**
     * @param src 消息来源
     * @param message   发送目标
     */
    public void sendMessage(ClientConnection src, Message message) throws IOException {
        System.out.println(message);
        // 所有人，广播消息
        if (message.getId() == 0) {
            clients.values().forEach(client -> dispatchMessage(client, src.getClientName(), "所有人", message.getMessage()));
        } else {
            // 单独发送某一个人
            int targetUser = message.getId();
            ClientConnection target = clients.get(targetUser);
            if (target == null) {
                System.err.println("用户" + targetUser + "不存在");
            } else {
                dispatchMessage(target, src.getClientName(), "你", message.getMessage());
            }
        }
    }

    /**
     * 用户下线
     * @param clientConnection 下线用户
     */
    public void clientOffline(ClientConnection clientConnection) {
        clients.remove(clientConnection.getClientId());
        clients.values().forEach(client -> dispatchMessage(client, "系统", "所有人", clientConnection.getClientName() + "下线了" + getAllClientsInfo()));
    }

    /**
     * @param client 连接
     * @param src   消息来源
     * @param target   发送目标
     * @param message   消息
     */
    private void dispatchMessage(ClientConnection client, String src, String target, String message) {
        try {
            client.sendMessage(src + "对" + target + "说" + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAllClientsInfo() {
        return clients.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().getClientName()).collect(Collectors.joining(","));
    }

}
