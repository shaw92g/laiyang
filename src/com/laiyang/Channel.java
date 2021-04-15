package com.laiyang;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class Channel {
    private PriorityQueue<Message> queue;

    // a random number between 0 and 9999
    private int randInt = new Random().nextInt(100 - 1) + 1;

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
