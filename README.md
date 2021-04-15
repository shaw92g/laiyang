# Lai-Yang Distributed Global Snapshot Recording Algorithm implemented in Java

## Requirements
* You should have Java Runtime Environment (1.8 and above) installed on your system 
* It can be run on Windows, Linux or iOS

## How to run the code?
* Verify that java is installed an working on your terminal by executing ``java -version`` 
* To run the jar execute``java -jar laiyang/out/artifacts/laiyang_jar/laiyang.jar``
* This executable has been tested to be working in iOS and Windows.

## Assumptions:
* Distributed processes are simulated using threads
* Channels re simulated using priority queues with different priority condition for FIFO and non FIFO modes.
    * For FIFO, priority order = system timestamp value (in milli sec) (in ascending order)
    * For non FIFO, priority order = system timestamp value (in milli sec) % 10
    * Since priority queues in Java are not thread safe, access to these queues are synchronized inside the channel class.
* Every message has three parts ``{creationTimestamp, amount, color}``
* Every process sends min 3 and max 5 messages to other prcesses randomly,
* Each message transfers an amount of Rs 20 from sender to the receiver process
* All distributed processes if inactive (no events occur in them) for 5sec terminate automatically.

## Inputs
* Input 1: Number of processes in the distributed system (``n``) (recommended value 3)
* Input 2: processId that initiates the algorithm (``i``). ``0 <= i <= n-1``
* Input 3: Channel mode (FIFO = ``0``, NON-FIFO = ``1``)

## Output
* Logs
    * Every process logs its events on the terminal like 
        * process start event
        * send event
        * receive event
        * color change event
        * local snapshot recording event
        * process stop event 
    * Each log line has its processId and color as prefix. e.g. ``[P2](RED):``
* Recorded Global Snapshot
    * The recorded global snapshot is presented at the end after all the processes, except the initiator process has terminated.
    * The initiator process presents a summary of the account balance of each process and the list of messages that were in transit when the global snapshot was taken.
    * Snapshot summary looks like:
    ```
    [P2](RED): Lai-Yang Global snapshot computation started
    [P2](RED): Global Snapshot:

    P0 = 60

    P1 = 120

    P2 = 60

    P0 to P1 --> [Message{1618482921854, 20, WHITE}]

    P0 to P2 --> [Message{1618482921220, 20, WHITE}]

    P1 to P2 --> [Message{1618482920342, 20, WHITE}]

    Global snapshot is consistent. Expeceted: 300, Actual: 300

    [P2](RED): Global snapshot computation ended
    ```