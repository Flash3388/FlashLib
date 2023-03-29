package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.AutoConnectingChannel;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;

public class BasicMessagingChannel implements MessagingChannel {

    protected final NetChannel mChannel;
    private final InstanceId mOurId;
    private final Logger mLogger;

    private final ByteBuffer mReadBuffer;
    private final MessageReadingContext mReadingContext;
    private final MessageSerializer mSerializer;

    public BasicMessagingChannel(Function<Runnable, ? extends NetChannel> channelCreator,
                                 InstanceId ourId,
                                 Logger logger,
                                 KnownMessageTypes messageTypes) {
        mReadBuffer = ByteBuffer.allocateDirect(1024);
        mReadingContext = new MessageReadingContext(messageTypes);
        mOurId = ourId;
        mLogger = logger;
        mSerializer = new MessageSerializer(ourId);

        mChannel = channelCreator.apply(()-> {
            mLogger.debug("Channel connected, reset data");

            mReadBuffer.clear();
            mReadingContext.clear();
        });
    }

    public BasicMessagingChannel(NetChannelConnector connector,
                                 SocketAddress remote,
                                 InstanceId ourId,
                                 Logger logger,
                                 KnownMessageTypes messageTypes) {
        this((onConnector)-> new AutoConnectingChannel(connector, remote, logger, onConnector),
                ourId, logger, messageTypes);
    }

    @Override
    public void processUpdates(UpdateHandler handler) throws IOException {
        mReadBuffer.clear();
        IncomingData data = mChannel.read(mReadBuffer);
        if (data.getBytesReceived() < 1) {
            return;
        }

        mReadBuffer.rewind();
        mReadingContext.updateBuffer(mReadBuffer, data.getBytesReceived());

        Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
        if (resultOptional.isPresent()) {
            MessageReadingContext.ParseResult parseResult = resultOptional.get();

            mLogger.debug("New message received: sender={}, type={}",
                    parseResult.getInfo().getSender(),
                    parseResult.getInfo().getType());
            handler.onNewMessage(parseResult.getInfo(), parseResult.getMessage());
        }
    }

    @Override
    public void write(MessageType type, OutMessage message) throws IOException {
        byte[] content = mSerializer.serialize(type, message);
        mChannel.write(ByteBuffer.wrap(content));
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
