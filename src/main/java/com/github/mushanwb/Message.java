package com.github.mushanwb;

// 发送和接收消息的格式
public class Message {
    // 发送给客户端的 id
    private int id;
    // 发送给客户端的 信息
    private String message;

    public Message() {

    }

    public Message(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
