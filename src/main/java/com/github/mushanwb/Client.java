package com.github.mushanwb;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

// 客户端
public class Client {

    public static void main(String[] args) throws IOException {
        System.out.println("输入你的昵称");
        Scanner userInput = new Scanner(System.in);
        String name = new Scanner(System.in).next();

        Socket socket = new Socket("127.0.0.1", 8080);

        socket.getOutputStream().write(name.getBytes());
        socket.getOutputStream().write('\n');
        socket.getOutputStream().flush();

        System.out.println("连接成功");

        new Thread(() -> randFromServer(socket)).start();

        // 死循环从服务器读写数据
        while (true) {
            System.out.println("输入你要发送的聊天消息");
            System.out.println("id:message, 例如, 1:hello 代表向 id 为 1 的用户发送 hello 消息");
            System.out.println("id = 0 代表向所有人发送消息, 例如, 0:hello 代表向 所有在线用户发送 hello 消息");

            String line = userInput.nextLine();
            if (!line.contains(":")) {
                System.out.println("输入的 id 格式不对");
            } else {
                int colonIndex = line.indexOf(':');
                int id = Integer.parseInt(line.substring(0, colonIndex));
                String message = line.substring(colonIndex + 1);

                String json = JSON.toJSONString(new Message(id, message));

                socket.getOutputStream().write(json.getBytes());
                socket.getOutputStream().write('\n');
                socket.getOutputStream().flush();
            }

        }
    }

    private static void randFromServer(Socket socket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
