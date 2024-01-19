package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServerMessagingChannelImpl implements ServerMessagingChannel {

    private final NetServerChannel mChannel;
    private final Clock mClock;

    private final ServerUpdateHandler mUpdateHandler;
    private final MessageSerializer mSerializer;

    public ServerMessagingChannelImpl(NetServerChannel channel,
                                      InstanceId ourId,
                                      KnownMessageTypes messageTypes,
                                      Clock clock,
                                      Logger logger) {
        mChannel = channel;
        mClock = clock;

        mUpdateHandler = new ServerUpdateHandler(messageTypes, logger);
        mSerializer = new MessageSerializer(ourId);
    }


    @Override
    public void processUpdates(UpdateHandler handler) throws IOException {
        mUpdateHandler.mClientUpdateHandler = handler;
        mChannel.processUpdates(mUpdateHandler);
    }

    @Override
    public void write(Message message) throws IOException {
        // TODO: OPTIMIZE FOR REUSING BUFFERS
        Time now = mClock.currentTime();
        byte[] content = mSerializer.serialize(now, message);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        mChannel.writeToAll(buffer);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }

    private static class ServerUpdateHandler implements NetServerChannel.UpdateHandler {

        private final KnownMessageTypes mMessageTypes;
        private final Logger mLogger;

        private final Map<NetClientInfo, ClientNode> mClients;
        private ServerMessagingChannel.UpdateHandler mClientUpdateHandler;

        private ServerUpdateHandler(KnownMessageTypes messageTypes, Logger logger) {
            mMessageTypes = messageTypes;
            mLogger = logger;

            mClients = new HashMap<>();
            mClientUpdateHandler = null;
        }

        @Override
        public void onClientConnected(NetClientInfo clientInfo) {
            if (mClientUpdateHandler == null) {
                return;
            }

            mLogger.debug("New client in server: {}", clientInfo.getAddress());

            ClientNode node = new ClientNode(mMessageTypes, mLogger);
            mClients.put(clientInfo, node);
        }

        @Override
        public void onClientDisconnected(NetClientInfo clientInfo) {
            if (mClientUpdateHandler == null) {
                return;
            }

            mLogger.debug("Client disconnected from server: {}", clientInfo.getAddress());

            ClientNode node = mClients.remove(clientInfo);
            if (node != null) {
                node.onDisconnect(mClientUpdateHandler);
            }
        }

        @Override
        public void onNewData(NetClientInfo sender, ByteBuffer buffer, int amountReceived) throws IOException {
            buffer.rewind();

            if (mClientUpdateHandler == null) {
                return;
            }

            ClientNode node = mClients.get(sender);
            if (node == null) {
                mLogger.error("Received data from unknown client, sender={}", sender);
                return;
            }

            node.onNewData(mClientUpdateHandler, buffer, amountReceived);
        }
    }

    private static class ClientNode {

        private final Logger mLogger;
        private final MessageReadingContext mReadingContext;
        private InstanceId mInstanceId;

        private ClientNode(KnownMessageTypes messageTypes, Logger logger) {
            mLogger = logger;
            mReadingContext = new MessageReadingContext(messageTypes, logger);
            mInstanceId = null;
        }

        public void onNewData(ServerMessagingChannel.UpdateHandler updateHandler, ByteBuffer buffer, int amountReceived) throws IOException {
            mReadingContext.updateBuffer(buffer, amountReceived);

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

                    if (mInstanceId == null) {
                        mInstanceId = header.getSender();

                        try {
                            updateHandler.onClientConnected(mInstanceId);
                        } catch (Throwable t) {
                            mLogger.error("Unexpected error from UpdateHandler.onClientConnected. Ignored", t);
                        }
                    }

                    try {
                        updateHandler.onNewMessage(header, message);
                    } catch (Throwable t) {
                        mLogger.error("Unexpected error from UpdateHandler.onNewMessage. Ignored", t);
                    }

                    // we managed to parse the message, so we can start trying to parse the next one
                    hasMoreToParse = true;

                    // TODO: THE SERVER ALSO NEEDS TO ROUTE ALL THE MESSAGES TO CLIENTS, MAYBE JUST
                    //  CALL WRITEALL? OR LET IT BE DONE BY THE UPDATEHANDLER?
                } else {
                    hasMoreToParse = false;
                }
            } while (hasMoreToParse);
        }

        public void onDisconnect(ServerMessagingChannel.UpdateHandler updateHandler) {
            if (mInstanceId == null) {
                return;
            }

            try {
                updateHandler.onClientDisconnected(mInstanceId);
            } catch (Throwable t) {
                mLogger.error("Unexpected error from UpdateHandler.onClientDisconnected. Ignored", t);
            }
        }
    }
}
