package com.flash3388.flashlib.net.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.ConnectedNetChannel;
import com.flash3388.flashlib.net.NetConnector;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class TcpClientConnector implements NetConnector {

    private final Logger mLogger;

    private SocketChannel mBaseChannel;
    private ConnectedNetChannel mChannel;
    private boolean mLastAttemptError;

    public TcpClientConnector(Logger logger) {
        mLogger = logger;
        mBaseChannel = null;
        mChannel = null;
        mLastAttemptError = false;
    }

    @Override
    public ConnectedNetChannel connect(SocketAddress remote, Time timeout) throws IOException, TimeoutException, InterruptedException {
        if (mLastAttemptError) {
            mLastAttemptError = false;

            // wait before retrying
            Thread.sleep(timeout.valueAsMillis());
        }

        SocketChannel channel = openChannel();

        try {
            if (!channel.connect(remote)) {
                while (!channel.finishConnect()) {
                    //noinspection BusyWait
                    Thread.sleep(timeout.valueAsMillis());
                }
            }
        } catch (IOException e) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
            mLastAttemptError = true;
            throw e;
        } catch (InterruptedException e) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
            throw e;
        }

        try {
            mChannel = new BlockingConnectedTcpChannel(mBaseChannel);
            return mChannel;
        } catch (IOException e) {
            mLastAttemptError = true;
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
            mBaseChannel = null;
        } else if (mBaseChannel != null) {
            Closeables.silentClose(mBaseChannel);
            mChannel = null;
            mBaseChannel = null;
        }
    }

    private SocketChannel openChannel() throws IOException {
        if (mBaseChannel == null) {
            try {
                mLogger.debug("Opening new TCP CLIENT socket in non-blocking mode");

                mBaseChannel = SocketChannel.open();
                mBaseChannel.configureBlocking(false);
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        return mBaseChannel;
    }
}
