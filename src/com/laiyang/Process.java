package com.laiyang;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.Collectors;

public class Process extends Thread {
    private int processId;
    private Channel[][] channelMatrix;
    private int numberOfProcesses;
    private Color color = Color.WHITE;
    private int balance = Config.initialProcessBalance;
    private History history;

    private String processName;
    private Random rand = new Random();
    private int sendCount = rand.nextInt(Config.maxMessageCount - Config.minMessageCount) + Config.minMessageCount;
    private long startTimestamp = System.currentTimeMillis();
    private long lastReceivedTimestamp = startTimestamp;
    private boolean isInitiator = false;
    private boolean isGSRecordingInitiated = false;
    private Snapshot localSnapshot;
    private Snapshot[] globalSnapshot;

    public Process(int processId, Channel[][] channelMatrix) {
        this.processId = processId;
        this.channelMatrix = channelMatrix;
        this.numberOfProcesses = channelMatrix.length;
        this.processName =  "P" + processId;
        this.history = new History(processId);
    }

    public Process(int processId, Channel[][] channelMatrix, boolean isInitiator, Snapshot[] globalSnapshot) {
        this(processId, channelMatrix);
        this.isInitiator = isInitiator;
        this.globalSnapshot = globalSnapshot;
    }

    @Override
    public void run()
    {
        long currTimestamp;
        System.out.format("%s: Started (sendCount = %d)\n", getProcessLabel(), sendCount);

        while(true) {
            try {
                sleep(rand.nextInt(Config.receiveDelayLimit));
                currTimestamp = System.currentTimeMillis();
                receiveMoney();

                if(sendCount > 0) {
                    int randomProcessId = rand.nextInt(numberOfProcesses);
                    while(randomProcessId == processId) {
                        randomProcessId = rand.nextInt(numberOfProcesses);
                    }


                    sleep(rand.nextInt(Config.sendDelayLimit));
                    // transfer Rs x to a random process
                    sendMoney(randomProcessId, Config.trasnferAmount);

                    sendCount--;
                }

                // start snapshot recording after x milli sec from the process start
                if(!isGSRecordingInitiated && isInitiator && (currTimestamp - startTimestamp) >= Config.recordingStartDelay) {
                    System.out.format("\n%s: Lai-Yang Global Snapshot recording algorithm started. Initiator records its own snapshot\n", getProcessLabel());
                    recordLocalSnapshot();

                    // prevent further initiation of the Algo
                    isGSRecordingInitiated = true;
                }

                // end process if no message is received for the last x milli sec and there is no message to be sent
                if((currTimestamp - lastReceivedTimestamp) >= Config.inactiveDuration && sendCount == 0) {
                    if(color == Color.WHITE) {
                        System.out.format("\n%s: Stop signal triggered. Record Local snapshot.\n", getProcessLabel());
                        recordLocalSnapshot();
                    }

                    if(isInitiator) {
                        computeGlobalSnapshot();
                    }

                    System.out.format("%s: Stopped\n", getProcessLabel());
                    break;
                }
            }
            catch (Exception e) {
                // Throwing an exception
                System.out.format("%s: Exception is caught", getProcessLabel());
                e.printStackTrace();
            }
        }

    }

    private Snapshot getSnapshot(){
        return new Snapshot(processId, balance, history.getSendHistory(), history.getReceiveHistory());
    }

    private void recordLocalSnapshot() {
        if(color == Color.WHITE){
            localSnapshot = getSnapshot();
            System.out.format("\n%s: Local snapshot recorded: \n%s\n", getProcessLabel(), localSnapshot);
            color = Color.RED;
            System.out.format("\n%s: Process turns RED\n", getProcessLabel());

            // update global snapshot to be processed by the initiator
            globalSnapshot[processId] = localSnapshot;
        }
    }

    private boolean sendMoney(int receiverId, int amount){
        Message m = generateMessage(amount);
        balance = balance - amount;
        channelMatrix[processId][receiverId].addMessage(m);
        System.out.format("%s: Sent %s to P%d\n", getProcessLabel(), m, receiverId);
        return history.recordSend(receiverId, m);
    }

    private int receiveMoney(){
        int count = 0;
        for(int i = 0; i<numberOfProcesses; i++) {
            if(i != processId) {
                Message m = channelMatrix[i][processId].getMessage();
                if(m != null) {
                    if(m.getColor() == Color.RED && color == Color.WHITE){
                        System.out.format("\n%s: RED %s arrived from P%d. Record Local snapshot\n", getProcessLabel(), m, i);
                        recordLocalSnapshot();
                    }

                    balance = balance + m.getAmount();
                    System.out.format("%s: Received %s from P%d\n", getProcessLabel(), m, i);
                    count++;
                    history.recordReceive(i, m);
                }
            }
        }

        if(count > 0) {
            lastReceivedTimestamp = System.currentTimeMillis();
        }

        return count;
    }

    private Message generateMessage(int amount){
        return new Message(System.currentTimeMillis(), amount, color);
    }

    private String getProcessLabel() {
        return String.format("[%s](%s)", processName, color);
    }

    private void computeGlobalSnapshot() {
        // wait till all processes have have recorded their snapshot
        while(Arrays.stream(globalSnapshot).filter(x -> x != null).toArray().length < numberOfProcesses);

        System.out.println("--------------------------------------");
        System.out.format("\n%s: Lai-Yang Global snapshot computation started", getProcessLabel());


        for(int i = 0; i < numberOfProcesses; i++){
            for(ListIterator<SendRecord> itt = globalSnapshot[i].getSendRecords().listIterator(); itt.hasNext();){
                SendRecord sendRecord = itt.next();

                // remove all message from sendHistory that were received
                if(globalSnapshot[sendRecord.getReceiverId()].isMessageReceived(sendRecord.message)){
                    itt.remove();
                }
            }
        }

        System.out.format("\n%s: Global Snapshot:\n", getProcessLabel());
        int total = 0;

        for(int i = 0; i < numberOfProcesses; i++){
            Snapshot s = globalSnapshot[i];
            System.out.format("\nP%s = %d\n", i, s.getBalance());
            total = total + s.getBalance();
        }

        for(int i = 0; i < numberOfProcesses; i++){
            Snapshot s = globalSnapshot[i];
            for(int j = 0; j < numberOfProcesses; j++) {
                int finalJ = j;
                List<SendRecord> inTransit = s.getSendRecords().stream().filter(x -> x.getReceiverId() == finalJ).collect(Collectors.toList());

                if(inTransit.size() > 0) {
                    System.out.format("\nP%d to P%d --> %s\n", i, j, inTransit.stream().map(x -> x.getMessage()).collect(Collectors.toList()));
                    total = total + inTransit.stream().map(x -> x.getMessage().getAmount()).mapToInt(Integer::intValue).sum();
                }
            }
        }

        if(total == numberOfProcesses * Config.initialProcessBalance) {
            System.out.print("\nGlobal snapshot is consistent. ");
        } else {
            System.out.print("\nGlobal snapshot is NOT consistent. ");
        }

        System.out.format("Expeceted: %d, Actual: %d\n",
            numberOfProcesses * Config.initialProcessBalance, total);

        System.out.format("\n%s: Global snapshot computation ended\n", getProcessLabel());
        System.out.println("--------------------------------------");
    }
}