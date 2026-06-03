package com.avis.app.ptalk.domain.model;

public class DeviceChatLog {
    private String chatLogId;
    private long timestamp; // unix timestamp

    public DeviceChatLog(String chatLogId, long timestamp) {
        this.chatLogId = chatLogId;
        this.timestamp = timestamp;
    }

    public DeviceChatLog() {

    }


    public String getChatLogId() {
        return chatLogId;
    }

    public void setChatLogId(String chatLogId) {
        this.chatLogId = chatLogId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
