package com.example.socialmediaapp.model;

public class ModelMessage {
    private String messageId;
    private String senderId;
    private String message;
    private String timestamp;

    public ModelMessage() {
        // Default constructor required for Firestore
    }

    public ModelMessage(String messageId, String senderId, String message, String timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
