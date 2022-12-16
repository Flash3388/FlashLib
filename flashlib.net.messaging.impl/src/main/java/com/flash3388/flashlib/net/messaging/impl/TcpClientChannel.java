package com.flash3388.flashlib.net.messaging.impl;

import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingFunction;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClientChannel implements Closeable {

    private static final int CONNECTION_WAIT_TIME = 100;

    private final SocketAddress mRemoteAddress;

    private final Lock mLock;
    private final Condition mConnected;
    private final AtomicBoolean mConnector;

    private SocketChannel mBaseChannel;
    private TcpSocketChannel mChannel;

    public TcpClientChannel(SocketAddress remoteAddress) {
        mRemoteAddress = remoteAddress;

        mLock = new ReentrantLock();
        mConnected = mLock.newCondition();
        mConnector = new AtomicBoolean(false);

        mBaseChannel = null;
        mChannel = null;
    }

    public synchronized void waitForConnection() throws IOException, InterruptedException {
        //noinspection resource
        connectChannel();
    }

    public synchronized void write(ByteBuffer buffer) throws IOException, InterruptedException {
        try {
            //noinspection resource
            TcpSocketChannel channel = connectChannel();
            channel.write(buffer);
        } catch (IOException | InterruptedException e) {
            close();
            throw e;
        }
    }

    public synchronized <T> T read(ThrowingFunction<BufferedReader, T, IOException> func) throws IOException, InterruptedException {
        try {
            //noinspection resource
            TcpSocketChannel channel = connectChannel();
            return func.apply(channel.reader());
        } catch (IOException | InterruptedException e) {
            close();
            throw e;
        }
    }

    private TcpSocketChannel connectChannel() throws IOException, InterruptedException {
        mLock.lock();
        try {
            if (mChannel == null) {
                if (!mConnector.getAndSet(true)) {
                    try {
                        doConnection();
                    } catch (IOException e) {
                        mConnector.set(false);
                        throw e;
                    }
                } else {
                    mConnected.await();
                }
            }

            return mChannel;
        } finally {
            mLock.unlock();
        }
    }

    private SocketChannel openChannel() throws IOException {
        mLock.lock();
        try {
            if (mBaseChannel == null) {
                try {
                    mBaseChannel = SocketChannel.open();
                    mBaseChannel.configureBlocking(false);
                } catch (IOException e) {
                    close();
                    throw e;
                }
            }

            return mBaseChannel;
        } finally {
            mLock.unlock();
        }
    }

    private void doConnection() throws InterruptedException, IOException {
        while (true) {
            SocketChannel channel = openChannel();

            try {
                if (!channel.connect(mRemoteAddress)) {
                    while (!channel.finishConnect()) {
                        //noinspection BusyWait
                        Thread.sleep(CONNECTION_WAIT_TIME);
                    }
                }
            } catch (IOException e) {
                Closeables.silentClose(mBaseChannel);
                mBaseChannel = null;

                // wait some time before retry
                //noinspection BusyWait
                Thread.sleep(CONNECTION_WAIT_TIME);

                continue;
            } catch (InterruptedException e) {
                Closeables.silentClose(mBaseChannel);
                mBaseChannel = null;
                throw e;
            }

            try {
                mChannel = new TcpSocketChannel(mBaseChannel);
            } catch (IOException e) {
                close();
                throw e;
            }

            mConnected.signalAll();

            break;
        }
    }

    @Override
    public void close() {
        mLock.lock();
        try {
            if (mChannel != null) {
                Closeables.silentClose(mChannel);
                mChannel = null;
                mBaseChannel = null;
            } else if (mBaseChannel != null) {
                Closeables.silentClose(mBaseChannel);
                mChannel = null;
                mBaseChannel = null;
            }

            mConnector.set(false);
        } finally {
            mLock.unlock();
        }
    }
}
