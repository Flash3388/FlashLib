package com.flash3388.flashlib.net.tcp;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.ConnectedNetChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class RoutingTcpService extends SingleUseService {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;

    private TcpServerChannel mChannel;
    private Thread mThread;

    public RoutingTcpService(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;

        mThread = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mChannel = new TcpServerChannel(mBindAddress, mLogger);

        mThread = new Thread(
                new Task(mChannel, mLogger),
                "RoutingServer-Task");
        mThread.setDaemon(true);

        mThread.start();
    }

    @Override
    protected void stopRunning() {
        mThread.interrupt();
        mThread = null;

        Closeables.silentClose(mChannel);
        mChannel = null;
    }

    private static class Task implements Runnable {

        private final TcpServerChannel mChannel;
        private final Logger mLogger;
        private final UpdateHandler mUpdateHandler;

        private Task(TcpServerChannel channel, Logger logger) {
            mChannel = channel;
            mLogger = logger;
            mUpdateHandler = new UpdateHandler(channel);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.handleUpdates(mUpdateHandler);
                } catch (TimeoutException e) {
                    // no need to do anything
                } catch (Throwable t) {
                    mLogger.error("Error in RoutingServer.Task", t);
                }
            }
        }
    }

    private static class UpdateHandler implements TcpServerChannel.UpdateHandler {

        private final TcpServerChannel mChannel;
        private final ByteBuffer mReadBuffer;

        private UpdateHandler(TcpServerChannel channel) {
            mChannel = channel;
            mReadBuffer = ByteBuffer.allocateDirect(1024);
        }

        @Override
        public void onNewChannel(ConnectedNetChannel channel) throws IOException {

        }

        @Override
        public void onNewData(ConnectedNetChannel channel) throws IOException {
            mReadBuffer.clear();

            try {
                int read = channel.read(mReadBuffer);
                if (read > 0) {
                    mReadBuffer.flip();
                    mChannel.writeToAllBut(mReadBuffer, channel);
                }
            } catch (TimeoutException e) {
                throw new IOException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(e);
            }
        }
    }
}
