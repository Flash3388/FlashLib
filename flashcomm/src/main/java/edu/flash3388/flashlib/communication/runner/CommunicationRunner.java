package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.connection.Connection;
import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.communication.message.Messenger;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.util.concurrent.ExecutorCloser;
import edu.flash3388.flashlib.util.flow.SingleUseParameterizedRunner;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommunicationRunner extends SingleUseParameterizedRunner<Connection> {

    private final ExecutorService mExecutorService;
    private final Serializer mSerializer;
    private final Logger mLogger;

    private final Consumer<Message> mNewMessagesConsumer;
    private final Supplier<Optional<Message>> mMessagesToSendSupplier;

    private final AtomicReference<Connection> mConnectionReference;

    public CommunicationRunner(ExecutorService executorService, Serializer serializer, Logger logger, Consumer<Message> newMessageConsumer, Supplier<Optional<Message>> messagesToSendSupplier) {
        mExecutorService = executorService;
        mSerializer = serializer;
        mLogger = logger;

        mNewMessagesConsumer = newMessageConsumer;
        mMessagesToSendSupplier = messagesToSendSupplier;

        mConnectionReference = new AtomicReference<>();
    }

    @Override
    protected void startRunner(Connection connection) {
        mConnectionReference.set(connection);

        Messenger messenger = new Messenger(connection, mSerializer);

        mExecutorService.execute(new MessageReadTask(messenger, mNewMessagesConsumer, mLogger));
        mExecutorService.execute(new MessageWriteTask(messenger, mMessagesToSendSupplier, mLogger));
    }

    @Override
    protected void stopRunner() {
        try {
            Closer closer = Closer.empty();

            Connection connection = mConnectionReference.getAndSet(null);
            closer.add(connection);

            closer.add(new ExecutorCloser(mExecutorService));

            closer.close();
        } catch (IOException e) {
            mLogger.log(Level.SEVERE, "error while closing resources", e);
        }
    }
}
