package com.flash3388.flashlib.net.message;

import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.udp.BroadcastUdpChannel;
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

public class UdpMessagingChannel implements MessagingChannel {

    private final InstanceId mOurId;
    private final MessageWriter mMessageWriter;
    private final MessageReader mMessageReader;
    private final Clock mClock;
    private final Logger mLogger;

    private final BroadcastUdpChannel mChannel;
    private final ByteBuffer mReadBuffer;

    public UdpMessagingChannel(int bindPort,
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

        mChannel = new BroadcastUdpChannel(bindPort, logger);
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
            MessageReader.Result result = mMessageReader.read(dataInputStream);

            if (result.senderId.equals(mOurId)) {
                // since we broadcast, this could happen so just ignore
                throw new TimeoutException();
            }

            Time now = mClock.currentTime();
            MessageInfo messageInfo = new MessageInfoImpl(result.senderId, now, result.type);

            handler.onNewMessage(messageInfo, result.message);
        }
    }

    @Override
    public void write(MessageType type, Message message) throws IOException, InterruptedException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            mMessageWriter.write(dataOutputStream, type, message);
            dataOutputStream.flush();

            mLogger.debug("Sending BROADCAST of message");

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());
            mChannel.broadcast(buffer);
        }
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
