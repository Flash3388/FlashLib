package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.channels.ServerUpdate;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerMessagingChannelImpl implements ServerMessagingChannel {

    private static final Time READ_TIMEOUT = Time.milliseconds(20);

    private final NetServerChannel mChannel;
    private final KnownMessageTypes mMessageTypes;
    private final Clock mClock;
    private final Logger mLogger;

    private final MessageSerializer mSerializer;
    private final Map<SocketAddress, ClientNode> mClients;
    private final Set<ClientNode> mClientsToDisconnect;
    private final ByteBuffer mReadBuffer;

    public ServerMessagingChannelImpl(NetServerChannel channel,
                                      InstanceId ourId,
                                      KnownMessageTypes messageTypes,
                                      Clock clock,
                                      Logger logger) {
        mChannel = channel;
        mMessageTypes = messageTypes;
        mClock = clock;
        mLogger = logger;

        mSerializer = new MessageSerializer(ourId);
        mClients = new ConcurrentHashMap<>();
        mClientsToDisconnect = new CopyOnWriteArraySet<>();
        mReadBuffer = ByteBuffer.allocateDirect(1024);
    }

    @Override
    public void processUpdates(UpdateHandler handler) throws IOException {
        disconnectClientsInErrorList(handler);

        ServerUpdate update;
        try {
            update = mChannel.readNextUpdate(READ_TIMEOUT);
        } catch (TimeoutException ignore) {
            return;
        }

        try {
            switch (update.getType()) {
                case NONE:
                    return;
                case NEW_CLIENT:
                    handleNewClient();
                    break;
                case NEW_DATA:
                    handleNewData(update.getClientAddress(), handler);
                    break;
            }
        } finally {
            update.done();
        }
    }

    @Override
    public void write(Message message, boolean onlyForServer) throws IOException {
        // TODO: OPTIMIZE FOR REUSING BUFFERS
        Time now = mClock.currentTime();
        byte[] content = mSerializer.serialize(now, message, onlyForServer);
        ByteBuffer buffer = ByteBuffer.wrap(content);

        write(buffer, null);
    }

    @Override
    public void close() throws IOException {
        Closeables.silentClose(mChannel);
        mClients.clear();
        mClientsToDisconnect.clear();

        // TODO: HOW TO REPORT OF DISCONNECTION OF ALL CLIENTS AS RESULT OF THIS?
        //      IF CLOSED EXTERNALLY, THEN IT SHOULDN'T MATTER
    }

    private void handleNewClient() throws IOException {
        NetClient client = mChannel.acceptNewClient();
        NetClientInfo info = client.getInfo();

        mLogger.debug("Server received new client from {}",
                info.getAddress());

        mClients.put(info.getAddress(), new ClientNode(client, mMessageTypes, mLogger));
    }

    private void handleNewData(SocketAddress clientAddress, UpdateHandler updateHandler) throws IOException {
        ClientNode node = mClients.get(clientAddress);
        if (node == null) {
            // no such client
            mLogger.error("Received data from unknown client at {}", clientAddress);
            return;
        }

        mReadBuffer.clear();
        node.mReadLock.lock();
        try {
            IncomingData data = node.mClient.read(mReadBuffer);
            if (data == null || data.getBytesReceived() < 1) {
                disconnectClientImmediate(node, updateHandler);
                return;
            }

            if (data.getBytesReceived() >= 1) {
                mLogger.debug("New data from client: {}, size={}",
                        data.getSender(),
                        data.getBytesReceived());

                mReadBuffer.rewind();
                node.mReadingContext.updateBuffer(mReadBuffer, data.getBytesReceived());
            }

            parseNewData(node, updateHandler);
        } catch (IOException | RuntimeException | Error e) {
            mLogger.error("Error processing new data from client at {}", node, e);
            disconnectClientImmediate(node, updateHandler);
            throw e;
        } finally {
            node.mReadLock.unlock();
        }
    }

    private void parseNewData(ClientNode node, UpdateHandler updateHandler) throws IOException {
        boolean hasMoreToParse;
        do {
            Optional<MessageReadingContext.ParseResult> resultOptional = node.mReadingContext.parse();
            if (resultOptional.isPresent()) {
                MessageReadingContext.ParseResult parseResult = resultOptional.get();

                MessageHeader header = parseResult.getHeader();
                Message message = parseResult.getMessage();
                mLogger.debug("New message received: sender={}, type={}",
                        header.getSender(),
                        header.getMessageType());

                if (node.mInstanceId == null) {
                    node.mInstanceId = header.getSender();

                    mLogger.info("New client connected and identified {}, at {}",
                            node.mInstanceId,
                            node.getInfo().getAddress());

                    try {
                        updateHandler.onClientConnected(node.mInstanceId);
                    } catch (Throwable t) {
                        mLogger.error("Unexpected error from UpdateHandler.onClientConnected. Ignored", t);
                    }
                }

                try {
                    updateHandler.onNewMessage(header, message);
                } catch (Throwable t) {
                    mLogger.error("Unexpected error from UpdateHandler.onNewMessage. Ignored", t);
                }

                if (!header.isOnlyForServer()) {
                    try {
                        // TODO: OPTIMIZE FOR REUSING BUFFERS
                        byte[] content = mSerializer.serialize(header, message);
                        ByteBuffer buffer = ByteBuffer.wrap(content);

                        write(buffer, node);
                    } catch (Throwable t) {
                        mLogger.error("Error while routing received messages to other clients", t);
                    }
                }

                hasMoreToParse = true;
            } else {
                hasMoreToParse = false;
            }
        } while (hasMoreToParse);
    }

    private void write(ByteBuffer buffer, ClientNode skipClient) throws IOException {
        Set<ClientNode> erroredClients = new HashSet<>();

        ThrowableChain chain = Throwables.newChain();
        for (ClientNode node : mClients.values()) {
            try {
                if (skipClient != null && node == skipClient) {
                    continue;
                }

                mLogger.debug("Writing message to client: {}, size={}",
                        node,
                        buffer.limit());

                buffer.rewind();
                node.mWriteLock.lock();
                try {
                    node.mClient.write(buffer);
                } finally {
                    node.mWriteLock.unlock();
                }
            } catch (IOException | RuntimeException | Error e) {
                mLogger.error("Error sending data to client {}", node);

                chain.chain(e);
                erroredClients.add(node);
            }
        }

        mClientsToDisconnect.addAll(erroredClients);

        chain.throwIfType(IOException.class);
        chain.throwAsRuntime();
    }

    private void disconnectClientImmediate(ClientNode node, UpdateHandler updateHandler) {
        // should only be called from processUpdates

        NetClientInfo clientInfo = node.getInfo();

        mClients.remove(clientInfo.getAddress());
        Closeables.silentClose(node.mClient);

        if (node.mInstanceId != null) {
            try {
                updateHandler.onClientDisconnected(node.mInstanceId);
            } catch (Throwable t) {
                // the client is already disconnected, so not much to do here.
                mLogger.error("Unexpected error from UpdateHandler.onClientDisconnected. Ignored", t);
            }
        }
    }

    private void disconnectClientsInErrorList(UpdateHandler updateHandler) {
        // should only be called from processUpdates
        if (mClientsToDisconnect.isEmpty()) {
            return;
        }

        for (Iterator<ClientNode> it = mClientsToDisconnect.iterator();
             it.hasNext(); ) {
            ClientNode node = it.next();
            try {
                disconnectClientImmediate(node, updateHandler);
            } finally {
                it.remove();
            }
        }
    }

    private static class ClientNode {

        final NetClient mClient;

        final MessageReadingContext mReadingContext;
        // readLock can hold writeLock but not reversed. IMPORTANT!
        final Lock mReadLock;
        final Lock mWriteLock;

        InstanceId mInstanceId;

        private ClientNode(NetClient client,
                           KnownMessageTypes messageTypes,
                           Logger logger) {
            mClient = client;

            mReadingContext = new MessageReadingContext(messageTypes, logger);
            mReadLock = new ReentrantLock();
            mWriteLock = new ReentrantLock();

            mInstanceId = null;
        }

        public NetClientInfo getInfo() {
            return mClient.getInfo();
        }

        @Override
        public String toString() {
            return mClient.getInfo().toString();
        }
    }
}
