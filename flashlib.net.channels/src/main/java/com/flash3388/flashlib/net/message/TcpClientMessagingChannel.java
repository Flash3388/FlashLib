package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.AutoConnectingChannel;
import com.flash3388.flashlib.net.BufferedChannelReader;
import com.flash3388.flashlib.net.tcp.TcpClientConnector;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class TcpClientMessagingChannel implements MessagingChannel {

    private final NewDataHandler mDataHandler;
    private final MessageSerializer mSerializer;
    private final Logger mLogger;

    private final AutoConnectingChannel mChannel;
    private final ByteBuffer mReadBuffer;

    public TcpClientMessagingChannel(SocketAddress serverAddress,
                                     NewDataHandler dataHandler,
                                     MessageSerializer serializer,
                                     Logger logger) {
        mDataHandler = dataHandler;
        mSerializer = serializer;
        mLogger = logger;

        mChannel = new AutoConnectingChannel(new TcpClientConnector(logger), serverAddress, logger);
        mReadBuffer = ByteBuffer.allocateDirect(1024);
    }

    @Override
    public void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException {
        // TODO: RECOGNIZE NEW REMOTE
        mChannel.waitForConnection();

        BufferedChannelReader reader = new BufferedChannelReader(mChannel, mReadBuffer);
        reader.clear();

        // this will block until we receive data
        try (DataInputStream dataInputStream = new DataInputStream(reader)) {
            NewDataHandler.Result result = mDataHandler.handle(dataInputStream);

            mLogger.debug("New message routed from server");
            handler.onNewMessage(result.info, result.message);
        }
    }

    @Override
    public void write(Message message) throws IOException, InterruptedException {
        mChannel.waitForConnection();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mSerializer.write(dataOutputStream, message);
            dataOutputStream.flush();

            mLogger.debug("Sending message to server");

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.write(buffer);
        }
    }
}
