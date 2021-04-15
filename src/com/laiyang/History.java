package com.laiyang;

import java.util.ArrayList;
import java.util.List;


public class History {
    private int proccessId;
    private List<SendRecord> sendHistory = new ArrayList<>();
    private List<ReceiveRecord> receiveHistory = new ArrayList<>();

    public History(int proccessId) {
        this.proccessId = proccessId;
    }

    public boolean recordSend(int receiverId, Message m){
        return sendHistory.add(new SendRecord(receiverId, m));
    }

    public boolean recordReceive(int senderId, Message m){
        return receiveHistory.add(new ReceiveRecord(senderId, m));
    }

    public List<SendRecord> getSendHistory() {
        return new ArrayList<>(sendHistory);
    }

    public List<ReceiveRecord> getReceiveHistory() {
        return new ArrayList<>(receiveHistory);
    }
}
