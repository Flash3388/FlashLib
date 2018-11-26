package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.communication.message.ReadException;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReadTask implements Runnable {

    private final Messenger mMessenger;
    private final BlockingQueue<Message> mMessagesQueue;
    private final Logger mLogger;

    public MessageReadTask(Messenger messenger, BlockingQueue<Message> messagesQueue, Logger logger) {
        mMessenger = messenger;
        mMessagesQueue = messagesQueue;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            // TODO: OFFER and READ should timeout/interrupted
            try {
                Message message = mMessenger.readMessage();
                mMessagesQueue.offer(message);
            } catch (ReadException e) {
                mLogger.log(Level.SEVERE, "error while reading message", e);
            }
        }
    }
}
