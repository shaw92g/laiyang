package com.laiyang;

import java.util.List;

public class Snapshot {
    private int processId;
    private int balance;
    private List<SendRecord> sendRecords;
    private List<ReceiveRecord> receiveRecords;

    public Snapshot(int processId, int balance, List<SendRecord> sendRecords, List<ReceiveRecord> receiveRecords) {
        this.processId = processId;
        this.balance = balance;
        this.sendRecords = sendRecords;
        this.receiveRecords = receiveRecords;
    }

    public List<SendRecord> getSendRecords() {
        return sendRecords;
    }

    public List<ReceiveRecord> getReceiveRecords() {
        return receiveRecords;
    }

    public boolean isMessageReceived(Message m){
        return receiveRecords.stream().filter(x -> x.getMessage() == m).toArray().length == 1;
    }

    public int getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "Snapshot of processId=" + processId +
                "\nbalance=" + balance +
                "\nsend=" + sendRecords +
                "\nreceive=" + receiveRecords;
    }
}
