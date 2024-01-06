package com.example.samvaad.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class ChatroomModel {
    String chatroomId;
    List<String> userIds;
    Timestamp lastmessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;


    public ChatroomModel() {
    }

    public ChatroomModel(String chatroomId, List<String> userIds, Timestamp lastmessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastmessageTimestamp = lastmessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastmessageTimestamp() {
        return lastmessageTimestamp;
    }

    public void setLastmessageTimestamp(Timestamp lastmessageTimestamp) {
        this.lastmessageTimestamp = lastmessageTimestamp;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

