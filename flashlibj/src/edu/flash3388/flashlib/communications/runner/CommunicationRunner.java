package edu.flash3388.flashlib.communications.runner;

import edu.flash3388.flashlib.communications.connection.Connector;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.runner.events.ConnectionListener;
import edu.flash3388.flashlib.event.Event;
import edu.flash3388.flashlib.event.EventDispatcher;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
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

    public synchronized void start(Connector connector, int connectionTimeout) {
        if (mTaskFuture != null) {
            throw new IllegalStateException("Already running");
        }

        ConnectionHandler connectionHandler = new ConnectionHandler(mEventDispatcher, mSerializer, mLogger);
        ConnectionTask connectionTask = new ConnectionTask(connector, connectionTimeout, connectionHandler, mLogger);

        mTaskFuture = mExecutorService.submit(connectionTask);
    }

    public synchronized boolean isRunning() {
        return mTaskFuture != null;
    }

    public synchronized void stop() {
        if (mTaskFuture == null) {
            throw new IllegalStateException("Not running");
        }

        mTaskFuture.cancel(true);
        mTaskFuture = null;
    }
}
