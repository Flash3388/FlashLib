package edu.flash3388.flashlib.communications.runner;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.io.Closer;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class ConnectionTask implements Runnable {

    private Connector mConnector;
    private int mConnectionTimeout;
    private Consumer<Connection> mConnectionConsumer;
    private Logger mLogger;

    ConnectionTask(Connector connector, int connectionTimeout, Consumer<Connection> connectionConsumer, Logger logger) {
        mConnector = connector;
        mConnectionTimeout = connectionTimeout;
        mConnectionConsumer = connectionConsumer;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                final Connection connection = mConnector.connect(mConnectionTimeout);
                mLogger.info("Connected");

                try {
                    Closer.with(connection).run(()-> {
                        mConnectionConsumer.accept(connection);

                        return null;
                    });
                } catch (IOException e) {
                    mLogger.log(Level.SEVERE, "Unexpected error while handling connection", e);
                }

                mLogger.info("Disconnected");
            } catch (ConnectionFailedException e) {
                mLogger.log(Level.SEVERE, "Connection failed", e);
            }
        }
    }
}
