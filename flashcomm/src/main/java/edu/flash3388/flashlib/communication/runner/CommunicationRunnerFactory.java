package edu.flash3388.flashlib.communication.runner;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.util.collections.BlockingQueueConsumer;
import edu.flash3388.flashlib.util.collections.BlockingQueueOptionalSupplier;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class CommunicationRunnerFactory {

    private CommunicationRunnerFactory() {}

    public static CommunicationRunner createWithQueues(BlockingQueue<Message> messagesReceivedQueue, BlockingQueue<Message> messagesToSendQueue, Logger logger) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Serializer serializer = new JavaObjectSerializer();

        Consumer<Message> messagesConsumer = new BlockingQueueConsumer<>(messagesReceivedQueue);
        Supplier<Optional<Message>> messagesToSendSupplier = new BlockingQueueOptionalSupplier<>(messagesToSendQueue);

        return new CommunicationRunner(executorService, serializer, logger, messagesConsumer, messagesToSendSupplier);
    }
}
