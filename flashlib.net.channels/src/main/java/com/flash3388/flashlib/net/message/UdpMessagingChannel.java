package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.udp.MultiTargetUdpChannel;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class UdpMessagingChannel implements MessagingChannel {

    private final NewDataHandler mDataHandler;
    private final MessageSerializer mSerializer;
    private final Logger mLogger;

    private final MultiTargetUdpChannel mChannel;
    private final ByteBuffer mReadBuffer;

    public UdpMessagingChannel(int[] bindPorts,
                               NewDataHandler dataHandler,
                               MessageSerializer serializer,
                               Logger logger) {
        mDataHandler = dataHandler;
        mSerializer = serializer;
        mLogger = logger;

        mChannel = new MultiTargetUdpChannel(bindPorts, logger);
        mReadBuffer = ByteBuffer.allocate(1024);
    }

    @Override
    public void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException, TimeoutException {
        // TODO: RECOGNIZE NEW REMOTE
        mReadBuffer.rewind();
        // this will block until we receive something
        SocketAddress remoteAddress = mChannel.read(mReadBuffer);
        // TODO: do something with remote address?

        // parse packet
        int size = mReadBuffer.position();
        mReadBuffer.flip();

        mLogger.debug("Received new data packet from {}", remoteAddress);

        try (InputStream inputStream = new ByteArrayInputStream(mReadBuffer.array(), 0, size);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            NewDataHandler.Result result = mDataHandler.handle(dataInputStream);
            handler.onNewMessage(result.info, result.message);
        } catch (MessageSentByUsException e) {
            // since we broadcast, this could happen so just ignore
            throw new TimeoutException();
        }
    }

    @Override
    public void write(Message message) throws IOException, InterruptedException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mSerializer.write(dataOutputStream, message);
            dataOutputStream.flush();

            mLogger.debug("Sending BROADCAST of message");

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.broadcastToPossiblePorts(buffer);
        }
    }
}
