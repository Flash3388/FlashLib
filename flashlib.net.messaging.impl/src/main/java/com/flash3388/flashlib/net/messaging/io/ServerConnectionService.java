package com.flash3388.flashlib.net.messaging.io;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;

import java.io.IOException;

public class ServerConnectionService extends TerminalServiceBase {

    private final ServerMessagingChannel mChannel;
    private Thread mThread;

    public ServerConnectionService(ServerMessagingChannel channel) {
        mChannel = channel;
        mThread = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mThread = new Thread(new Task(mChannel), "Server-Connection-Acceptor");
        mThread.setDaemon(true);
        mThread.start();
    }

    @Override
    protected void stopRunning() {
        mThread.interrupt();
        mThread = null;
    }

    private static class Task implements Runnable {

        private final ServerMessagingChannel mChannel;

        private Task(ServerMessagingChannel channel) {
            mChannel = channel;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    mChannel.handleNewConnections();
                } catch (IOException e) {
                    // TODO: HANDLE
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
