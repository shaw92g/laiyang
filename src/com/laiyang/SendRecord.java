package com.laiyang;

public final class SendRecord extends Record {
    private int receiverId;

    public SendRecord(int receiverId, Message message) {
        super(message);
        this.receiverId = receiverId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    @Override
    public String toString() {
        return "{" +
                "to=" + receiverId +
                ", " + message +
                '}';
    }
}
