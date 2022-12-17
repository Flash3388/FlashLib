package com.flash3388.flashlib.net.messaging.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.messaging.ConnectionEvent;
import com.flash3388.flashlib.net.messaging.ConnectionListener;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageReceiver;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessagingService extends TerminalServiceBase implements MessageQueue, MessageReceiver {

    private final MessagingChannel mChannel;
    private final MessagingServerChannel mServerChannel;
    private final EventController mEventController;
    private final Logger mLogger;
    private final BlockingQueue<Message> mMessageQueue;

    private Thread mWriteThread;
    private Thread mReadThread;
    private Thread mServerUpdateThread;

    private MessagingService(MessagingChannel channel, MessagingServerChannel serverChannel, EventController eventController, Logger logger) {
        mChannel = channel;
        mServerChannel = serverChannel;
        mEventController = eventController;
        mLogger = logger;
        mMessageQueue = new LinkedBlockingQueue<>();

        mWriteThread = null;
        mReadThread = null;
        mServerUpdateThread = null;
    }

    public static MessagingService server(MessagingServerChannel channel, EventController controller, Logger logger) {
        return new MessagingService(channel, channel, controller, logger);
    }

    public static MessagingService client(MessagingChannel channel, EventController controller, Logger logger) {
        return new MessagingService(channel, null, controller, logger);
    }

    @Override
    public void add(Message message) {
        mMessageQueue.add(message);
    }

    @Override
    public void addListener(MessageListener listener) {
        mEventController.registerListener(listener);
    }

    @Override
    public void addListener(ConnectionListener listener) {
        mEventController.registerListener(listener);
    }

    @Override
    protected void startRunning() throws ServiceException {
        mWriteThread = new Thread(
                new WriteTask(mChannel, mMessageQueue, mLogger),
                "MessagingService-WriteTask");
        mWriteThread.setDaemon(true);

        mReadThread = new Thread(
                new ReadTask(mChannel, mEventController, mLogger),
                "MessagingService-ReadTask");
        mReadThread.setDaemon(true);

        mChannel.setOnConnection(()-> {
            mEventController.fire(
                    new ConnectionEvent(),
                    ConnectionEvent.class,
                    ConnectionListener.class,
                    ConnectionListener::onConnection
            );
        });

        if (mServerChannel != null) {
            mServerUpdateThread = new Thread(
                    new ServerUpdateTask(mServerChannel, mLogger),
                    "MessagingService-ServerUpdate");
            mServerUpdateThread.setDaemon(true);
            mServerUpdateThread.start();
        }

        mWriteThread.start();
        mReadThread.start();
    }

    @Override
    protected void stopRunning() {
        mChannel.setOnConnection(null);

        mWriteThread.interrupt();
        mWriteThread = null;

        mReadThread.interrupt();
        mReadThread = null;

        if (mServerUpdateThread != null) {
            mServerUpdateThread.interrupt();
            mServerUpdateThread = null;
        }
    }
}
