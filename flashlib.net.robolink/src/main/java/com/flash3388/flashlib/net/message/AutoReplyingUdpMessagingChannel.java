package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.udp.BasicUdpChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class AutoReplyingUdpMessagingChannel implements MessagingChannel {

    private static final Time READ_TIMEOUT = Time.milliseconds(500);

    private final InstanceId mOurId;
    private final MessageWriter mMessageWriter;
    private final MessageReader mMessageReader;
    private final Clock mClock;
    private final Logger mLogger;

    private final BasicUdpChannel mChannel;
    private final ByteBuffer mReadBuffer;
    private final AtomicReference<SocketAddress> mLastReceivedAddress;

    public AutoReplyingUdpMessagingChannel(int bindPort,
                                           InstanceId ourId,
                                           MessageWriter messageWriter,
                                           MessageReader messageReader,
                                           Clock clock,
                                           Logger logger) {
        mOurId = ourId;
        mMessageWriter = messageWriter;
        mMessageReader = messageReader;
        mClock = clock;
        mLogger = logger;

        mChannel = new BasicUdpChannel(bindPort, READ_TIMEOUT, logger);
        mReadBuffer = ByteBuffer.allocate(1024);
        mLastReceivedAddress = new AtomicReference<>(null);
    }

    @Override
    public void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException, TimeoutException {
        mReadBuffer.clear();
        // this will block until we receive something
        SocketAddress remoteAddress = mChannel.read(mReadBuffer);

        // parse packet
        int size = mReadBuffer.position();
        mReadBuffer.flip();

        mLogger.debug("Received new data packet (size={}) from {}", size, remoteAddress);

        try (InputStream inputStream = new ByteArrayInputStream(mReadBuffer.array(), 0, size);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            MessageReader.Result result = mMessageReader.read(dataInputStream);

            Time now = mClock.currentTime();
            MessageInfo messageInfo = new MessageInfoImpl(result.senderId, now, result.type);

            handler.onNewMessage(messageInfo, result.message);

            mLastReceivedAddress.set(remoteAddress);
        }
    }

    @Override
    public void write(MessageType type, Message message) throws IOException, InterruptedException {
        SocketAddress remoteAddress = mLastReceivedAddress.get();
        if (remoteAddress == null) {
            mLogger.debug("No remoteAddress configured yet, can't send");
            return;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mMessageWriter.write(dataOutputStream, type, message);
            dataOutputStream.flush();

            mLogger.debug("Sending message to {}", remoteAddress);

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.writeTo(buffer, remoteAddress);
        }
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
