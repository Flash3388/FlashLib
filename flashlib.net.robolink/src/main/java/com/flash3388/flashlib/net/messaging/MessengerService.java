package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.castle.concurrent.service.ServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannelImpl;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.tcp.TcpChannelOpener;
import com.flash3388.flashlib.net.channels.tcp.TcpServerChannelOpener;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MessengerService extends ServiceBase implements Messenger {

    private static final Logger LOGGER = Logging.getLogger("Comm", "Messenger");

    private final ChannelUpdater mChannelUpdater;
    private final InstanceId mOurId;
    private final Clock mClock;
    private final EventController mEventController;
    private final KnownMessageTypes mKnownMessageTypes;

    private Supplier<Context> mContextSupplier;
    private Context mContext;

    public MessengerService(ChannelUpdater channelUpdater, InstanceId ourId, Clock clock) {
        mChannelUpdater = channelUpdater;
        mOurId = ourId;
        mClock = clock;

        mEventController = Controllers.newSyncExecutionController();
        mKnownMessageTypes = new KnownMessageTypes();

        mKnownMessageTypes.put(PingMessage.TYPE);

        mContextSupplier = null;
        mContext = null;
    }

    public void configureServer(SocketAddress bindAddress) {
        if (isRunning()) {
            throw new IllegalStateException("cannot reconfigure while running");
        }

        mContextSupplier = new ServerContextCreator(
                mOurId,
                mClock,
                bindAddress,
                LOGGER,
                mEventController,
                mKnownMessageTypes,
                mChannelUpdater);
    }

    public void configureClient(SocketAddress serverAddress) {
        if (isRunning()) {
            throw new IllegalStateException("cannot reconfigure while running");
        }

        mContextSupplier = new ClientContextSupplier(
                mOurId,
                mClock,
                serverAddress,
                LOGGER,
                mEventController,
                mKnownMessageTypes,
                mChannelUpdater);
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
        if (mContext == null) {
            throw new IllegalStateException("no channel to queue messages");
        }

        LOGGER.debug("Queueing message of type {}", message.getType().getKey());
        mContext.queuer.accept(message);
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mContextSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mContext = mContextSupplier.get();
    }

    @Override
    protected void stopRunning() {
        if (mContext != null) {
            Closeables.silentClose(mContext.channel);
            mContext = null;
        }
    }

    private static class Context {

        public final Closeable channel;
        public final Consumer<Message> queuer;

        private Context(Closeable channel, Consumer<Message> queuer) {
            this.channel = channel;
            this.queuer = queuer;
        }
    }

    private static class ServerContextCreator implements Supplier<Context> {

        private final InstanceId mInstanceId;
        private final Clock mClock;
        private final SocketAddress mBindAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final ChannelUpdater mChannelUpdater;

        private ServerContextCreator(InstanceId instanceId,
                                     Clock clock,
                                     SocketAddress bindAddress,
                                     Logger logger,
                                     EventController eventController,
                                     KnownMessageTypes knownMessageTypes,
                                     ChannelUpdater channelUpdater) {
            mInstanceId = instanceId;
            mClock = clock;
            mBindAddress = bindAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mChannelUpdater = channelUpdater;
        }

        @Override
        public Context get() {
            ServerMessagingChannel channel = new ServerMessagingChannelImpl(
                    new TcpServerChannelOpener(mBindAddress, mLogger),
                    mChannelUpdater,
                    mKnownMessageTypes,
                    mInstanceId,
                    mClock,
                    mLogger);

            channel.setListener(new ServerChannelListener(channel, mEventController, mLogger));

            return new Context(channel, channel::queue);
        }
    }

    private static class ClientContextSupplier implements Supplier<Context> {

        private final InstanceId mInstanceId;
        private final Clock mClock;
        private final SocketAddress mServerAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final ChannelUpdater mChannelUpdater;

        private ClientContextSupplier(InstanceId instanceId,
                                      Clock clock,
                                      SocketAddress serverAddress,
                                      Logger logger,
                                      EventController eventController,
                                      KnownMessageTypes knownMessageTypes,
                                      ChannelUpdater channelUpdater) {
            mInstanceId = instanceId;
            mClock = clock;
            mServerAddress = serverAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mChannelUpdater = channelUpdater;
        }

        @Override
        public Context get() {
            ServerClock clock = new ServerClock(mClock, mLogger);

            MessagingChannel channel = new MessagingChannelImpl(
                    new TcpChannelOpener(mLogger),
                    mChannelUpdater,
                    mServerAddress,
                    mKnownMessageTypes,
                    mInstanceId,
                    clock,
                    mLogger);

            // TODO: NEED SOMETHING TO USE PINGCONTEXT
            PingContext pingContext = new PingContext(channel, clock, mLogger);
            channel.setListener(new ClientChannelListener(pingContext, mEventController, clock, mLogger));

            return new Context(channel, (message)-> channel.queue(message, false));
        }
    }

}
