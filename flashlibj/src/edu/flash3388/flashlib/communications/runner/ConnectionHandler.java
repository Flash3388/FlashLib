package edu.flash3388.flashlib.communications.runner;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.communications.message.*;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.runner.events.ConnectionEvent;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.communications.runner.events.DisconnectionEvent;
import edu.flash3388.flashlib.event.EventDispatcher;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConnectionHandler implements Consumer<Connection> {

    private EventDispatcher mEventDispatcher;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    ConnectionHandler(EventDispatcher eventDispatcher, PrimitiveSerializer serializer, Logger logger) {
        mEventDispatcher = eventDispatcher;
        mSerializer = serializer;
        mLogger = logger;
    }

    @Override
    public void accept(Connection connection) {
        MessageReader messageReader = new MessageReader(connection, mSerializer);
        MessageWriter messageWriter = new MessageWriter(connection, mSerializer);

        Queue<Message> messageQueue = new ConcurrentLinkedDeque<Message>();
        MessageQueue messageQueueWrapper = new MessageQueue(messageQueue);

        mEventDispatcher.dispatch(ConnectionListener.class, new ConnectionEvent(connection, messageQueueWrapper), ConnectionListener::onConnection);

        while (!Thread.interrupted()) {
            if (!handleNewMessages(messageReader, messageQueueWrapper)) {
                break;
            }
            if (!flushMessageQueue(messageQueue, messageWriter)) {
                break;
            }
        }

        mEventDispatcher.dispatch(ConnectionListener.class, new DisconnectionEvent(), ConnectionListener::onDisconnection);
    }

    private boolean handleNewMessages(MessageReader messageReader, MessageQueue messageQueue) {
        try {
            Message message = messageReader.readMessage();
            mEventDispatcher.dispatch(MessageListener.class, new MessageEvent(message, messageQueue),
                    MessageListener::onMessageReceived);
        } catch (ReadException e) {
            mLogger.log(Level.SEVERE, "Error while reading from connection", e);
            return false; // TODO: SHOULD WE REALLY DISCONNECT
        } catch (TimeoutException e) {
            // nothing we need to do, this might happen
        }

        return true;
    }

    private boolean flushMessageQueue(Queue<Message> messageQueue, MessageWriter messageWriter) {
        while (!messageQueue.isEmpty()) {
            Message message = messageQueue.remove();
            try {
                messageWriter.writeMessage(message);
            } catch (WriteException e) {
                mLogger.log(Level.SEVERE, "Error while writing to connection", e);
                return false; // TODO: SHOULD WE REALLY DISCONNECT
            }
        }

        return true;
    }
}
