package com.laiyang;

public final class Message {
    private long timestamp;
    private int amount;
    private Color color = Color.WHITE;

    public Message(long timestamp, int amount, Color color) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.color = color;
    }

    public Message(long timestamp, int amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAmount() {
        return amount;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Message{" + timestamp +
                ", " + amount +
                ", " + color +
                '}';
    }
}