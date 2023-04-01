package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.channels.tcp.TcpServerChannel;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.OutMessage;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class TcpServerMessagingChannel implements MessagingChannel {

    private final NetServerChannel mChannel;
    private final InstanceId mOurId;
    private final Logger mLogger;

    private final MessageReadingContext mReadingContext;
    private final AtomicReference<UpdateHandler> mHandler;
    private final Set<NetClientInfo> mKnownClients;
    private final MessageSerializer mSerializer;

    public TcpServerMessagingChannel(SocketAddress bindAddress,
                                     InstanceId ourId,
                                     Logger logger,
                                     KnownMessageTypes messageTypes) {
        mOurId = ourId;
        mLogger = logger;

        mReadingContext = new MessageReadingContext(messageTypes);
        mHandler = new AtomicReference<>();
        mKnownClients = new HashSet<>();
        mSerializer = new MessageSerializer(ourId);

        mChannel = new TcpServerChannel(bindAddress, logger, new NetServerChannel.UpdateHandler() {
            @Override
            public void onClientConnected(NetClientInfo clientInfo) {
                mLogger.debug("New client in server: {}", clientInfo.getAddress());
                mKnownClients.add(clientInfo);

                UpdateHandler handler = mHandler.get();
                if (handler != null) {
                    try {
                        Optional<List<MessageAndType>> optional = handler.getMessageForNewClient();
                        if (optional.isPresent()) {
                            List<MessageAndType> messages = optional.get();
                            for (MessageAndType message : messages) {
                                byte[] content = mSerializer.serialize(message.getType(), message.getMessage());
                                mChannel.writeToOne(ByteBuffer.wrap(content), clientInfo);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Error writing initial message to client", e);
                    }
                }
            }

            @Override
            public void onClientDisconnected(NetClientInfo clientInfo) {
                mLogger.debug("Client disconnected from server: {}", clientInfo.getAddress());
                mKnownClients.remove(clientInfo);
            }

            @Override
            public void onNewData(NetClientInfo sender, ByteBuffer buffer, int amountReceived) {
                buffer.rewind();
                mReadingContext.updateBuffer(buffer, amountReceived);

                try {
                    boolean hasMoreToParse;
                    do {
                        Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
                        if (resultOptional.isPresent()) {
                            MessageReadingContext.ParseResult parseResult = resultOptional.get();

                            mLogger.debug("New message received: sender={}, type={}",
                                    parseResult.getInfo().getSender(),
                                    parseResult.getInfo().getType().getKey());

                            hasMoreToParse = true;

                            UpdateHandler handler = mHandler.get();
                            if (handler != null) {
                                handler.onNewMessage(parseResult.getInfo(), parseResult.getMessage());
                            }

                            buffer.rewind();
                            mChannel.writeToAllBut(buffer, sender);
                        } else {
                            hasMoreToParse = false;
                        }
                    } while (hasMoreToParse || !mReadingContext.hasEnoughSpace());
                } catch (IOException e) {
                    logger.error("Error parsing message", e);
                }
            }
        });
    }

    @Override
    public synchronized void processUpdates(UpdateHandler handler) throws IOException {
        mHandler.set(handler);
        mChannel.processUpdates();
    }

    @Override
    public void write(MessageType type, OutMessage message) throws IOException {
        byte[] content = mSerializer.serialize(type, message);
        ByteBuffer buffer = ByteBuffer.wrap(content);
        mChannel.writeToAll(buffer);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
