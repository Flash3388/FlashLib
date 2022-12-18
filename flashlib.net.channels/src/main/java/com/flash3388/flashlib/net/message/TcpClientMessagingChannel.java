package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.AutoConnectingChannel;
import com.flash3388.flashlib.net.BufferedChannelReader;
import com.flash3388.flashlib.net.tcp.TcpClientConnector;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class TcpClientMessagingChannel implements MessagingChannel {

    private final MessageWriter mMessageWriter;
    private final MessageReader mMessageReader;
    private final Clock mClock;
    private final Logger mLogger;

    private final AutoConnectingChannel mChannel;
    private final ByteBuffer mReadBuffer;

    public TcpClientMessagingChannel(SocketAddress serverAddress,
                                     MessageWriter messageWriter,
                                     MessageReader messageReader,
                                     Clock clock,
                                     Logger logger) {
        mMessageWriter = messageWriter;
        mMessageReader = messageReader;
        mClock = clock;
        mLogger = logger;

        mChannel = new AutoConnectingChannel(new TcpClientConnector(logger), serverAddress, logger);
        mReadBuffer = ByteBuffer.allocateDirect(1024);
    }

    @Override
    public void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException {
        mChannel.waitForConnection();

        BufferedChannelReader reader = new BufferedChannelReader(mChannel, mReadBuffer);
        reader.clear();

        // this will block until we receive data
        try (DataInputStream dataInputStream = new DataInputStream(reader)) {
            MessageReader.Result result = mMessageReader.read(dataInputStream);

            Time now = mClock.currentTime();
            MessageInfo messageInfo = new MessageInfoImpl(result.senderId, now);

            mLogger.debug("New message routed from server");
            handler.onNewMessage(messageInfo, result.message);
        } catch (IOException e) {
            mLogger.debug("Error processing incoming message", e);
            throw e;
        }
    }

    @Override
    public void write(Message message) throws IOException, InterruptedException {
        mChannel.waitForConnection();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mMessageWriter.write(dataOutputStream, message);
            dataOutputStream.flush();

            mLogger.debug("Sending message to server");

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.write(buffer);
        } catch (IOException e) {
            mLogger.debug("Error processing outgoing message", e);
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
