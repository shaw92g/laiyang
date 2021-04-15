package com.laiyang;

import java.util.Scanner;

public class Main {
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Main: Distributed program started.");
        System.out.print("Main: Enter number of process: ");
        int n = scanner.nextInt();
        System.out.format("Main: Initiator ProcessId [0, %d]: ", n-1);
        int initatorProcessId = scanner.nextInt();

        Process[] processArr = new Process[n];
        Channel[][] channelMatrix = new Channel[n][n];
        Snapshot[] globalSnapshot = new Snapshot[n];

        System.out.print("Main: Enter channel mode [ FIFO(0) / NON-FIFO(1) ]: ");
        Channel.Mode mode = scanner.nextInt() == 0 ? Channel.Mode.FIFO : Channel.Mode.NON_FIFO;

        // create the channel matrix
        // channelMatrix[i][j] =  channel from Pi tp Pj
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                channelMatrix[i][j] = new Channel(mode);
            }
        }

        // Create and start all Processes
        for (int i = 0; i < n; i++) {
            processArr[i] = new Process(i, channelMatrix, initatorProcessId == i, globalSnapshot);
            processArr[i].start();
        }

        // Wait for all the processes to end
        for (int i = 0; i < n; i++) {
            try {
                processArr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nMain: Distributed program ended.");
    }
}