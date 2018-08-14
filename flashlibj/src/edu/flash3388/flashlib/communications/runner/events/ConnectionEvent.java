package edu.flash3388.flashlib.communications.runner.events;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.event.Event;

public class ConnectionEvent implements Event {

    private Connection mConnection;
    private MessageQueue mMessageQueue;

    public ConnectionEvent(Connection connection, MessageQueue messageQueue) {
        mConnection = connection;
        mMessageQueue = messageQueue;
    }

    public Connection getConnection() {
        return mConnection;
    }

    public MessageQueue getMessageQueue() {
        return mMessageQueue;
    }
}
