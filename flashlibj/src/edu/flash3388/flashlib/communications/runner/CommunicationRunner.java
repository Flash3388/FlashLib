package edu.flash3388.flashlib.communications.runner;

import edu.flash3388.flashlib.communications.connection.Connection;
import edu.flash3388.flashlib.communications.connection.ConnectionFailedException;
import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.communications.connection.TimeoutException;
import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageReader;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.ReadException;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.runner.events.ConnectionEvent;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.communications.runner.events.DisconnectionEvent;
import edu.flash3388.flashlib.event.Event;
import edu.flash3388.flashlib.event.EventDispatcher;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommunicationRunner {

    private ExecutorService mExecutorService;
    private EventDispatcher mEventDispatcher;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    private Future<?> mTaskFuture;

    public CommunicationRunner(ExecutorService executorService, PrimitiveSerializer serializer, Logger logger) {
        mExecutorService = executorService;
        mSerializer = serializer;
        mLogger = logger;

        mEventDispatcher = new EventDispatcher();
    }

    public void addMessageListener(Predicate<Event> eventPredicate, MessageListener listener) {
        mEventDispatcher.registerListener(eventPredicate, listener);
    }

    public void removeMessageListener(MessageListener listener) {
        mEventDispatcher.unregisterListener(listener);
    }

    public void addConnectionListener(Predicate<Event> eventPredicate, ConnectionListener listener) {
        mEventDispatcher.registerListener(eventPredicate, listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        mEventDispatcher.unregisterListener(listener);
    }

    public void start(Connector connector, int connectionTimeout) {
        if (mTaskFuture != null) {
            throw new IllegalStateException("Already running");
        }

        ConnectionHandler connectionHandler = new ConnectionHandler(mEventDispatcher, mSerializer, mLogger);
        ConnectionTask connectionTask = new ConnectionTask(connector, connectionTimeout, connectionHandler, mLogger);

        mTaskFuture = mExecutorService.submit(connectionTask);
    }

    public boolean isRunning() {
        return mTaskFuture != null;
    }

    public void stop() {
        if (mTaskFuture == null) {
            throw new IllegalStateException("Not running");
        }

        mTaskFuture.cancel(true);
        mTaskFuture = null;
    }

    private static class ConnectionTask implements Runnable {

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

    private static class ConnectionHandler implements Consumer<Connection> {

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
            mEventDispatcher.dispatch(ConnectionListener.class, new ConnectionEvent(connection), ConnectionListener::onConnection);

            MessageReader messageReader = new MessageReader(connection, mSerializer);
            MessageWriter messageWriter = new MessageWriter(connection, mSerializer);

            while (!Thread.interrupted()) {
                try {
                    Message message = messageReader.readMessage();
                    mEventDispatcher.dispatch(MessageListener.class, new MessageEvent(message, messageWriter),
                            MessageListener::onMessageReceived);
                } catch (ReadException e) {
                    mLogger.log(Level.SEVERE, "Error while reading from connection", e);
                    break; // TODO: SHOULD WE REALLY DISCONNECT
                } catch (TimeoutException e) {
                    // nothing we need to do, this might happen
                }
            }

            mEventDispatcher.dispatch(ConnectionListener.class, new DisconnectionEvent(), ConnectionListener::onDisconnection);
        }
    }
}
