package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.castle.concurrent.service.ServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.messsaging.BaseMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ClientMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.PingMessage;
import com.flash3388.flashlib.net.channels.messsaging.ServerClock;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ServerMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.tcp.TcpChannelOpener;
import com.flash3388.flashlib.net.channels.tcp.TcpServerChannelOpener;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class MessengerService extends ServiceBase implements Messenger {

    private static final Logger LOGGER = Logging.getLogger("Comm", "Messenger");

    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    private final Clock mClock;
    private final EventController mEventController;
    private final KnownMessageTypes mKnownMessageTypes;

    private Supplier<BaseMessagingChannel> mContextSupplier;
    private BaseMessagingChannel mChannel;

    public MessengerService(ChannelUpdater channelUpdater, ChannelId ourId, Clock clock) {
        mChannelUpdater = channelUpdater;
        mOurId = ourId;
        mClock = clock;

        mEventController = Controllers.newSyncExecutionController();
        mKnownMessageTypes = new KnownMessageTypes();

        mKnownMessageTypes.put(PingMessage.TYPE);

        mContextSupplier = null;
        mChannel = null;
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
        if (mChannel == null) {
            throw new IllegalStateException("no channel to queue messages");
        }

        LOGGER.debug("Queueing message of type {}", message.getType().getKey());
        mChannel.queue(message);
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mContextSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mChannel = mContextSupplier.get();
        mChannel.start();
    }

    @Override
    protected void stopRunning() {
        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
        }
    }

    private static class ServerContextCreator implements Supplier<BaseMessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final SocketAddress mBindAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final ChannelUpdater mChannelUpdater;

        private ServerContextCreator(ChannelId id,
                                     Clock clock,
                                     SocketAddress bindAddress,
                                     Logger logger,
                                     EventController eventController,
                                     KnownMessageTypes knownMessageTypes,
                                     ChannelUpdater channelUpdater) {
            mId = id;
            mClock = clock;
            mBindAddress = bindAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mChannelUpdater = channelUpdater;
        }

        @Override
        public BaseMessagingChannel get() {
            ServerMessagingChannel channel = new ServerMessagingChannelImpl(
                    new TcpServerChannelOpener(mBindAddress, mLogger),
                    mChannelUpdater,
                    mKnownMessageTypes,
                    mId,
                    mClock,
                    mLogger);

            channel.setListener(new ServerChannelListener(channel, mEventController, mLogger));

            return channel;
        }
    }

    private static class ClientContextSupplier implements Supplier<BaseMessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final SocketAddress mServerAddress;
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final ChannelUpdater mChannelUpdater;

        private ClientContextSupplier(ChannelId id,
                                      Clock clock,
                                      SocketAddress serverAddress,
                                      Logger logger,
                                      EventController eventController,
                                      KnownMessageTypes knownMessageTypes,
                                      ChannelUpdater channelUpdater) {
            mId = id;
            mClock = clock;
            mServerAddress = serverAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mChannelUpdater = channelUpdater;
        }

        @Override
        public BaseMessagingChannel get() {
            ClientMessagingChannel channel = new ClientMessagingChannel(
                    new TcpChannelOpener(mLogger),
                    mChannelUpdater,
                    mKnownMessageTypes,
                    mId,
                    new ServerClock(mClock, mLogger),
                    mLogger,
                    mServerAddress);

            channel.setListener(new ClientChannelListener(mEventController, mLogger));
            channel.enableKeepAlive();

            return channel;
        }
    }

}
