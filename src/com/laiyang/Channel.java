package com.laiyang;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Channel {
    private PriorityQueue<Message> queue;

    public enum Mode{
        FIFO, NON_FIFO
    }

    public Channel(Mode mode) {
        if(mode == Mode.FIFO) {
            queue = new PriorityQueue<>(Comparator.comparingLong(Message::getTimestamp));
        } else {
            queue = new PriorityQueue<>(Comparator.comparingLong(i -> (i.getTimestamp() % 10)));
        }
    }

    public synchronized boolean addMessage(Message m) {
        return queue.add(m);
    }

    public synchronized Message getMessage() {
        if(!queue.isEmpty()) {
            return queue.remove();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                queue +
                '}';
    }
}
