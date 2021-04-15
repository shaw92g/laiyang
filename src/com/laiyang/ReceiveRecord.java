package com.laiyang;

public final class ReceiveRecord extends Record {
    private int senderId;

    public ReceiveRecord(int senderId, Message message) {
        super(message);
        this.senderId = senderId;
    }

    public int getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return "{" +
                "from=" + senderId +
                ", " + message +
                '}';
    }
}