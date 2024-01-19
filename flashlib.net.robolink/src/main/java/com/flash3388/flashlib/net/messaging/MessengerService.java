package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.tcp.TcpClientConnector;
import com.flash3388.flashlib.net.channels.tcp.TcpServerChannel;
import com.flash3388.flashlib.net.util.NetServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

public class MessengerService extends NetServiceBase implements Messenger {

    private final InstanceId mInstanceId;
    private final Clock mClock;
    private final Logger mLogger;
    private final EventController mEventController;
    private final KnownMessageTypes mKnownMessageTypes;
    private final BlockingQueue<Message> mWriteQueue;

    private Function<Messenger, Context> mContextSupplier;
    private Context mContext;

    public MessengerService(InstanceId instanceId, Clock clock, Logger logger) {
        mInstanceId = instanceId;
        mClock = clock;
        mLogger = logger;

        mEventController = Controllers.newSyncExecutionController();
        mKnownMessageTypes = new KnownMessageTypes();
        mWriteQueue = new LinkedBlockingQueue<>();

        mKnownMessageTypes.put(PingMessage.TYPE);

        mContextSupplier = null;
        mContext = null;
    }

    public void configureServer(SocketAddress bindAddress) {
        if (isRunning()) {
            throw new IllegalStateException("cannot reconfigure while running");
        }

        mContextSupplier = new ServerContextCreator(
                mInstanceId,
                mClock,
                bindAddress,
                mLogger,
                mEventController,
                mKnownMessageTypes,
                mWriteQueue);
    }

    public void configureClient(SocketAddress serverAddress) {
        if (isRunning()) {
            throw new IllegalStateException("cannot reconfigure while running");
        }

        mContextSupplier = new ClientContextSupplier(
                mInstanceId,
                mClock,
                serverAddress,
                mLogger,
                mEventController,
                mKnownMessageTypes,
                mWriteQueue);
    }

    public RegisteredListener addListener(ConnectionListener listener) {
        mEventController.registerListener(listener);
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public void registerMessageTypes(Collection<? extends MessageType> types) {
        mKnownMessageTypes.putAll(types);
    }

    @Override
    public RegisteredListener addListener(MessageListener listener) {
        mEventController.registerListener(listener);
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public RegisteredListener addListener(MessageListener listener, Set<? extends MessageType> types) {
        mEventController.registerListener(listener, new MessageTypeListenerPredicate(types));
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public void send(Message message) {
        mLogger.debug("Queueing message of type {}", message.getType().getKey());
        mWriteQueue.add(message);
    }

    @Override
    protected Map<String, Runnable> createTasks() {
        if (mContextSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mContext = mContextSupplier.apply(this);

        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("MessengerService-ReadTask", mContext.readTask);
        tasks.put("MessengerService-WriteTask", mContext.writeTask);

        return tasks;
    }

    @Override
    protected void freeResources() {
        if (mContext != null) {
            Closeables.silentClose(mContext.channel);
            mContext = null;
        }

        mWriteQueue.clear();
    }

    private static class Context {

        public final Closeable channel;
        public final Runnable readTask;
        public final Runnable writeTask;

        public Context(Closeable channel, Runnable readTask, Runnable writeTask) {
            this.channel = channel;
            this.readTask = readTask;
            this.writeTask = writeTask;
        }
    }

    private static class ServerContextCreator implements Function<Messenger, Context> {

        private final InstanceId mInstanceId;
        private final Clock mClock;
        private final SocketAddress mBindAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final BlockingQueue<Message> mWriteQueue;

        private ServerContextCreator(InstanceId instanceId,
                                     Clock clock,
                                     SocketAddress bindAddress,
                                     Logger logger,
                                     EventController eventController,
                                     KnownMessageTypes knownMessageTypes,
                                     BlockingQueue<Message> writeQueue) {
            mInstanceId = instanceId;
            mClock = clock;
            mBindAddress = bindAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mWriteQueue = writeQueue;
        }

        @Override
        public Context apply(Messenger messenger) {
            ServerMessagingChannel channel = new ServerMessagingChannelImpl(
                    new TcpServerChannel(mBindAddress, mLogger),
                    mInstanceId,
                    mKnownMessageTypes,
                    mClock,
                    mLogger);

            Runnable readTask = new ServerReadTask(
                    messenger,
                    channel,
                    mEventController,
                    mLogger);

            Runnable writeTask = new WriteTask(
                    channel,
                    mWriteQueue,
                    mLogger);

            return new Context(channel, readTask, writeTask);
        }
    }

    private static class ClientContextSupplier implements Function<Messenger, Context> {

        private final InstanceId mInstanceId;
        private final Clock mClock;
        private final SocketAddress mServerAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final BlockingQueue<Message> mWriteQueue;

        private ClientContextSupplier(InstanceId instanceId,
                                      Clock clock,
                                      SocketAddress serverAddress,
                                      Logger logger,
                                      EventController eventController,
                                      KnownMessageTypes knownMessageTypes,
                                      BlockingQueue<Message> writeQueue) {
            mInstanceId = instanceId;
            mClock = clock;
            mServerAddress = serverAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mWriteQueue = writeQueue;
        }

        @Override
        public Context apply(Messenger messenger) {
            ServerClock clock = new ServerClock(mClock);

            MessagingChannel channel = new BasicMessagingChannelImpl(
                    new TcpClientConnector(clock, mLogger),
                    mServerAddress,
                    mInstanceId,
                    clock,
                    mLogger,
                    mKnownMessageTypes);

            Runnable readTask = new ClientReadTask(
                    messenger,
                    channel,
                    mEventController,
                    clock,
                    mLogger);

            Runnable writeTask = new WriteTask(
                    channel,
                    mWriteQueue,
                    mLogger);

            return new Context(channel, readTask, writeTask);
        }
    }
}
