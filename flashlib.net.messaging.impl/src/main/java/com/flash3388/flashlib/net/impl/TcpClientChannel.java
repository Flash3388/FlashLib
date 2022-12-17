package com.flash3388.flashlib.net.impl;

import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingFunction;
import com.castle.util.function.ThrowingRunnable;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClientChannel implements Closeable {

    private static final int CONNECTION_WAIT_TIME = 100;

    private final SocketAddress mRemoteAddress;
    private final Logger mLogger;

    private final ByteBuffer mReadBuffer;
    private final Lock mLock;
    private final Condition mConnected;
    private final AtomicBoolean mConnector;
    private final AtomicReference<ThrowingRunnable<IOException>> mOnConnectionCallback;

    private SocketChannel mBaseChannel;
    private TcpSocketChannel mChannel;
    private BufferedReader mReader;

    public TcpClientChannel(SocketAddress remoteAddress, Logger logger) {
        mRemoteAddress = remoteAddress;
        mLogger = logger;

        mReadBuffer = ByteBuffer.allocateDirect(1024);
        mLock = new ReentrantLock();
        mConnected = mLock.newCondition();
        mConnector = new AtomicBoolean(false);
        mOnConnectionCallback = new AtomicReference<>(null);

        mBaseChannel = null;
        mChannel = null;
    }

    public void setOnConnection(ThrowingRunnable<IOException> callback) {
        mOnConnectionCallback.set(callback);
    }

    public void waitForConnection() throws IOException, InterruptedException {
        //noinspection resource
        connectChannel();
    }

    public void write(ByteBuffer buffer) throws IOException, InterruptedException {
        try {
            //noinspection resource
            TcpSocketChannel channel = connectChannel();
            channel.write(buffer);
        } catch (IOException | InterruptedException e) {
            close();
            throw e;
        }
    }

    public <T> T read(ThrowingFunction<BufferedReader, T, IOException> func) throws IOException, InterruptedException {
        try {
            //noinspection resource
            connectChannel();
            return func.apply(mReader);
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
                    mLogger.debug("Client opening new socket in non-blocking mode");

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
                mLogger.debug("Client attempting to connect to {}", mRemoteAddress);
                if (!channel.connect(mRemoteAddress)) {
                    while (!channel.finishConnect()) {
                        //noinspection BusyWait
                        Thread.sleep(CONNECTION_WAIT_TIME);
                    }
                }
            } catch (IOException e) {
                mLogger.debug("Client encountered error while connecting");

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

            mLogger.debug("Client established connection");
            try {
                mChannel = new TcpSocketChannel(mBaseChannel);
                mReader = new BufferedReader(mChannel, mReadBuffer);
                mReader.clear();

                ThrowingRunnable<IOException> callback = mOnConnectionCallback.get();
                if (callback != null) {
                    callback.run();
                }
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
            mLogger.debug("Client closing socket");

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
