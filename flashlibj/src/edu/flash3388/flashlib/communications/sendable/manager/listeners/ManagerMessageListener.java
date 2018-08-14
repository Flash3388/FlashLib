package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.ManagerMessageHandler;

import java.util.Collection;
import java.util.Optional;

public class ManagerMessageListener implements MessageListener {

    private Collection<ManagerMessageHandler> mHandlers;

    public ManagerMessageListener(Collection<ManagerMessageHandler> handlers) {
        mHandlers = handlers;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Message message = e.getMessage();
        MessageQueue messageQueue = e.getMessageQueue();

        Optional<ManagerMessageHandler> optionalMessageHandler = getHandler(message);
        if (optionalMessageHandler.isPresent()) {
            ManagerMessageHandler messageHandler = optionalMessageHandler.get();
            messageHandler.handle(message, messageQueue);
        }
    }

    private Optional<ManagerMessageHandler> getHandler(Message message) {
        for (ManagerMessageHandler messageHandler : mHandlers) {
            if (messageHandler.canHandle(message)) {
                return Optional.of(messageHandler);
            }
        }

        return Optional.empty();
    }
}
