package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.communication.message.WriteException;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageWriteTask implements Runnable {

    private final Messenger mMessenger;
    private final BlockingQueue<Message> mMessagesQueue;
    private final Logger mLogger;

    public MessageWriteTask(Messenger messenger, BlockingQueue<Message> messagesQueue, Logger logger) {
        mMessenger = messenger;
        mMessagesQueue = messagesQueue;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message message = mMessagesQueue.take();
                mMessenger.writeMessage(message);
            } catch (WriteException e) {
                mLogger.log(Level.SEVERE, "error while writing message", e);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
