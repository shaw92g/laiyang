package com.laiyang;

public class Record {
    protected Message message;

    public Record(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
