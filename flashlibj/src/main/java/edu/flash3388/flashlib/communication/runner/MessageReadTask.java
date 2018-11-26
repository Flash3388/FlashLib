package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.communication.message.ReadException;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReadTask implements Runnable {

    private final Messenger mMessenger;
    private final Consumer<Message> mMessagesConsumer;
    private final Logger mLogger;

    public MessageReadTask(Messenger messenger, Consumer<Message> messagesConsumer, Logger logger) {
        mMessenger = messenger;
        mMessagesConsumer = messagesConsumer;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message message = mMessenger.readMessage();
                mMessagesConsumer.accept(message);
            } catch (ReadException e) {
                mLogger.log(Level.SEVERE, "error while reading message", e);
            }
        }
    }
}
