package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.util.concurrent.ExecutorTerminator;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommunicationRunner {

    private final ExecutorService mExecutorService;
    private final Serializer mSerializer;
    private final Logger mLogger;

    private final AtomicReference<Connection> mConnectionReference;
    private final AtomicBoolean mIsTerminated;

    public CommunicationRunner(ExecutorService executorService, Serializer serializer, Logger logger) {
        mExecutorService = executorService;
        mSerializer = serializer;
        mLogger = logger;

        mConnectionReference = new AtomicReference<>();
        mIsTerminated = new AtomicBoolean(false);
    }

    public boolean isTerminated() {
        return mIsTerminated.get();
    }

    public boolean isRunning() {
        return mConnectionReference.get() != null;
    }

    public synchronized void start(Connection connection, BlockingQueue<Message> readQueue, BlockingQueue<Message> writeQueue) {
        if (isTerminated()) {
            throw new IllegalStateException("terminated");
        }
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        mConnectionReference.set(connection);

        Messenger messenger = new Messenger(connection, mSerializer);

        mExecutorService.execute(new MessageReadTask(messenger, readQueue, mLogger));
        mExecutorService.execute(new MessageWriteTask(messenger, writeQueue, mLogger));
    }

    public synchronized void terminate() {
        if (isTerminated()) {
            throw new IllegalStateException("already terminated");
        }
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        try {
            Closer closer = Closer.empty();

            Connection connection = mConnectionReference.getAndSet(null);
            closer.add(connection);

            closer.add(new ExecutorTerminator(mExecutorService));

            closer.close();
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "error while closing resources", e);
        }
    }
}
