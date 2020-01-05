package com.flash3388.flashlib.communication.runner;

import com.flash3388.flashlib.communication.message.Message;
import com.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import com.flash3388.flashlib.io.serialization.Serializer;
import com.flash3388.flashlib.util.collections.BlockingQueueConsumer;
import com.flash3388.flashlib.util.collections.BlockingQueueOptionalSupplier;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
