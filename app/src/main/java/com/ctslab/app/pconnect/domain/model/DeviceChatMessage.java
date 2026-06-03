package com.avis.app.ptalk.domain.model;

public class DeviceChatMessage {
    private String messageId;
    private String content;
    private int timestamp; // unix timestamp

    public DeviceChatMessage(String messageId, String content, int timestamp) {
        this.messageId = messageId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public DeviceChatMessage() {

    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
