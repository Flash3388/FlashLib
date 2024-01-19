package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.AutoConnectingChannel;
import com.flash3388.flashlib.net.channels.ChannelStateListener;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Optional;

public class BasicMessagingChannelImpl implements MessagingChannel {

    protected final NetChannel mChannel;
    private final Logger mLogger;

    private final ChannelStateListenerImpl mChannelStateListener;
    private final ByteBuffer mReadBuffer;
    private final MessageReadingContext mReadingContext;
    private final MessageSerializer mSerializer;

    public BasicMessagingChannelImpl(NetChannelConnector connector,
                                     SocketAddress remote,
                                     InstanceId ourId,
                                     Clock clock,
                                     Logger logger,
                                     KnownMessageTypes messageTypes) {
        mChannelStateListener = new ChannelStateListenerImpl(this);
        mChannel = new AutoConnectingChannel(connector, remote, logger, mChannelStateListener);
        mLogger = logger;

        mReadBuffer = ByteBuffer.allocateDirect(1024);
        mReadingContext = new MessageReadingContext(messageTypes, logger);
        mSerializer = new MessageSerializer(ourId, clock);
    }

    @Override
    public void processUpdates(UpdateHandler handler) throws IOException {
        mChannelStateListener.mClientUpdateHandler = handler;

        mReadBuffer.clear();
        IncomingData data = mChannel.read(mReadBuffer);
        if (data.getBytesReceived() >= 1) {
            mLogger.debug("New data from remote: {}, size={}",
                    data.getSender(),
                    data.getBytesReceived());

            mReadBuffer.rewind();
            mReadingContext.updateBuffer(mReadBuffer, data.getBytesReceived());
        }

        boolean hasMoreToParse;
        do {
            Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
            if (resultOptional.isPresent()) {
                MessageReadingContext.ParseResult parseResult = resultOptional.get();

                MessageHeader header = parseResult.getHeader();
                Message message = parseResult.getMessage();
                mLogger.debug("New message received: sender={}, type={}",
                        header.getSender(),
                        header.getMessageType());

                handler.onNewMessage(header, message);

                hasMoreToParse = true;
            } else {
                hasMoreToParse = false;
            }
        } while (hasMoreToParse);
    }

    @Override
    public void write(Message message) throws IOException {
        // TODO: OPTIMIZE FOR REUSING BUFFERS
        byte[] content = mSerializer.serialize(message);
        mChannel.write(ByteBuffer.wrap(content));
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }

    private static class ChannelStateListenerImpl implements ChannelStateListener {

        private final WeakReference<BasicMessagingChannelImpl> mChannel;
        private MessagingChannel.UpdateHandler mClientUpdateHandler;

        private ChannelStateListenerImpl(BasicMessagingChannelImpl channel) {
            mChannel = new WeakReference<>(channel);
        }

        @Override
        public void onConnect(NetClientInfo clientInfo) {
            BasicMessagingChannelImpl channel = mChannel.get();
            if (channel == null) {
                throw new IllegalStateException("channel garbage collected");
            }

            channel.mLogger.debug("Channel connected, reset data");

            channel.mReadBuffer.clear();
            channel.mReadingContext.clear();

            if (mClientUpdateHandler != null) {
                mClientUpdateHandler.onConnect();
            }
        }

        @Override
        public void onDisconnect(NetClientInfo clientInfo) {
            if (mClientUpdateHandler != null) {
                mClientUpdateHandler.onDisconnect();
            }
        }
    }
}
