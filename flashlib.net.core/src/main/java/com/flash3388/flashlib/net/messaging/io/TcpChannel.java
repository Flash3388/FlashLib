package com.flash3388.flashlib.net.messaging.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public abstract class TcpChannel {

    private final ChannelInput mInput;
    private final DataInput mInputStream;

    protected TcpChannel() {
        mInput = new ChannelInput(this, ByteBuffer.allocate(1024));
        mInputStream = new DataInputStream(mInput);
    }

    public ChannelOutput output() {
        return new ChannelOutput(this);
    }

    public DataInput input() {
        return mInputStream;
    }

    public void write(byte[] data) throws IOException {
        try {
            SocketChannel channel = getChannel();
            channel.write(ByteBuffer.wrap(data));
        } catch (IOException e) {
            closeChannel();
            throw e;
        }
    }

    public int read(ByteBuffer buffer) throws IOException {
        try {
            SocketChannel channel = getChannel();

            int read = channel.read(buffer);
            if (read < 0) {
                closeChannel();
                throw new ClosedChannelException();
            }

            buffer.flip();
            return read;
        } catch (IOException e) {
            closeChannel();
            throw e;
        }
    }

    protected abstract SocketChannel getChannel();
    protected abstract void closeChannel();
}
