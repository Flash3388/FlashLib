package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.communication.message.WriteException;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageWriteTask implements Runnable {

    private final Messenger mMessenger;
    private final Supplier<Optional<Message>> mMessagesSupplier;
    private final Logger mLogger;

    public MessageWriteTask(Messenger messenger, Supplier<Optional<Message>> messagesSupplier, Logger logger) {
        mMessenger = messenger;
        mMessagesSupplier = messagesSupplier;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Optional<Message> optionalMessage = mMessagesSupplier.get();
                if (!optionalMessage.isPresent()) {
                    continue;
                }

                Message message = optionalMessage.get();
                mMessenger.writeMessage(message);
            } catch (WriteException e) {
                mLogger.log(Level.SEVERE, "error while writing message", e);
            }
        }
    }
}
