package com.example.socialmediaapp.model;

import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ModelChatRoom {
    String chatroomId;
    List<String> userIds;
    String lastMessageTimestamp;
    String lastMessageSenderId;
    String lastMessage;
    List<ModelMessage> messages;

    public ModelChatRoom() {
    }

    public ModelChatRoom(String chatroomId, List<String> userIds, String lastMessageTimestamp, String lastMessageSenderId) {
        this.chatroomId = chatroomId;
        this.userIds = userIds;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderId = lastMessageSenderId;
        this.messages = new ArrayList<>();
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

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
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

    public List<ModelMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ModelMessage> messages) {
        this.messages = messages;
    }

    public String getFormattedTime() {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(lastMessageTimestamp));
        return DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
    }

}