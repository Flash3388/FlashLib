package edu.flash3388.flashlib.communications.runner.events;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.event.Event;

public class ConnectionEvent implements Event {

    private Connection mConnection;

    public ConnectionEvent(Connection connection) {
        mConnection = connection;
    }

    public Connection getConnection() {
        return mConnection;
    }
}
