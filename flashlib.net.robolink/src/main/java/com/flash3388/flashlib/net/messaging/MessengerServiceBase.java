package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.OutMessagingChannel;
import com.flash3388.flashlib.net.util.NetServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class MessengerServiceBase<T extends OutMessagingChannel> extends NetServiceBase implements Messenger {

    protected static final Logger LOGGER = Logging.getLogger("Comm", "MessengerService");

    protected final T mChannel;
    protected final EventController mEventController;
    protected final BlockingQueue<Message> mWriteQueue;

    public MessengerServiceBase(InstanceId instanceId,
                                Clock clock) {
        super(instanceId, clock);

        mEventController = Controllers.newSyncExecutionController();
        mWriteQueue = new LinkedBlockingQueue<>();

        // do this after initializing stuff which the channels need, so pretty much last
        mChannel = createChannel();
    }

    @Override
    public final RegisteredListener addListener(MessageListener listener) {
        mEventController.registerListener(listener);
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public final RegisteredListener addListener(MessageListener listener, Set<? extends MessageType> types) {
        mEventController.registerListener(listener, new MessageTypeListenerPredicate(types));
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public final void send(Message message) {
        LOGGER.debug("Queueing message of type {}", message.getType().getKey());
        mWriteQueue.add(message);
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("MessengerService-ReadTask", createReadTask());
        tasks.put("MessengerService-WriteTask",
                new WriteTask(mChannel, mWriteQueue, LOGGER));

        return tasks;
    }

    @Override
    protected void freeResources() {
        Closeables.silentClose(mChannel);
    }

    protected abstract T createChannel();
    protected abstract Runnable createReadTask();
}
