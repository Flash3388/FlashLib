package edu.flash3388.flashlib.communications.message;

import java.util.Queue;

public class MessageQueue {

    private Queue<Message> mMessagesQueue;

    public MessageQueue(Queue<Message> messageQueue) {
        mMessagesQueue = messageQueue;
    }

    public void enqueueMessage(Message message) {
        mMessagesQueue.add(message);
    }
}
