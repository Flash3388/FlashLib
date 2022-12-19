package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.BufferedChannelReader;
import com.flash3388.flashlib.net.ConnectedNetChannel;
import com.flash3388.flashlib.net.tcp.impl2.TcpServerChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Optional;

public class TcpServerMessagingChannel implements ServerMessagingChannel {

    private final MessageWriter mMessageWriter;
    private final MessageReader mMessageReader;
    private final Clock mClock;
    private final Logger mLogger;

    private final TcpServerChannel mChannel;
    private final ByteBuffer mReadBuffer;

    public TcpServerMessagingChannel(SocketAddress address,
                                     MessageWriter messageWriter,
                                     MessageReader messageReader,
                                     Clock clock,
                                     Logger logger) {
        mMessageWriter = messageWriter;
        mMessageReader = messageReader;
        mClock = clock;
        mLogger = logger;

        mChannel = new TcpServerChannel(address, logger);
        mReadBuffer = ByteBuffer.allocateDirect(1024);
    }

    @Override
    public void handleUpdates(UpdateHandler handler) throws IOException, TimeoutException {
        mChannel.handleUpdates(new TcpServerChannel.UpdateHandler() {
            @Override
            public void onNewChannel(ConnectedNetChannel channel) throws IOException {
                Optional<Message> optional = handler.onNewClientSend();
                if (!optional.isPresent()) {
                    return;
                }

                Message message = optional.get();

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                     DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                    mMessageWriter.write(dataOutputStream, message);
                    dataOutputStream.flush();

                    mLogger.debug("Sending message to a single client");

                    ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
                    channel.write(buffer);
                } catch (InterruptedException e) {
                    // re-interrupt
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onNewData(ConnectedNetChannel channel) throws IOException {
                BufferedChannelReader reader = new BufferedChannelReader(channel, mReadBuffer);
                reader.clear();

                // this will block until we receive data
                try (DataInputStream dataInputStream = new DataInputStream(reader)) {
                    MessageReader.Result result = mMessageReader.read(dataInputStream);

                    Time now = mClock.currentTime();
                    MessageInfo messageInfo = new MessageInfoImpl(result.senderId, now);

                    mLogger.debug("New message from clients {}" ,result.senderId);
                    handler.onNewMessage(messageInfo, result.message);
                } catch (IOException e) {
                    mLogger.debug("Error processing incoming message", e);
                    throw e;
                }
            }
        });
    }

    @Override
    public void write(Message message) throws IOException, InterruptedException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mMessageWriter.write(dataOutputStream, message);
            dataOutputStream.flush();

            mLogger.debug("Sending message to all clients");

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.writeToAll(buffer);
        } catch (IOException e) {
            mLogger.debug("Error processing outgoing message", e);
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        Closeables.silentClose(mChannel);
    }
}
