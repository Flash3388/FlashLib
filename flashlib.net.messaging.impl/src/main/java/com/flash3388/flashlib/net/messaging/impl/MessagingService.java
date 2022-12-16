package com.flash3388.flashlib.net.messaging.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageReceiver;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.flash3388.flashlib.net.messaging.io.MessagingServerChannel;
import com.notifier.EventController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessagingService extends TerminalServiceBase implements MessageQueue, MessageReceiver {

    private final MessagingChannel mChannel;
    private final MessagingServerChannel mServerChannel;
    private final EventController mEventController;
    private final BlockingQueue<Message> mMessageQueue;

    private Thread mWriteThread;
    private Thread mReadThread;
    private Thread mAcceptThread;

    private MessagingService(MessagingChannel channel, MessagingServerChannel serverChannel, EventController eventController) {
        mChannel = channel;
        mServerChannel = serverChannel;
        mEventController = eventController;
        mMessageQueue = new LinkedBlockingQueue<>();

        mWriteThread = null;
        mReadThread = null;
        mAcceptThread = null;
    }

    public static MessagingService server(MessagingServerChannel channel, EventController controller) {
        return new MessagingService(channel, channel, controller);
    }

    public static MessagingService client(MessagingChannel channel, EventController controller) {
        return new MessagingService(channel, null, controller);
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
    protected void startRunning() throws ServiceException {
        mWriteThread = new Thread(
                new WriteTask(mChannel, mMessageQueue),
                "MessagingService-WriteTask");
        mWriteThread.setDaemon(true);

        mReadThread = new Thread(
                new ReadTask(mChannel, mEventController),
                "MessagingService-ReadTask");
        mReadThread.setDaemon(true);

        if (mServerChannel != null) {
            mAcceptThread = new Thread(
                    new AcceptThread(mServerChannel),
                    "MessagingService-AcceptTask");
            mAcceptThread.setDaemon(true);
            mAcceptThread.start();
        }

        mWriteThread.start();
        mReadThread.start();
    }

    @Override
    protected void stopRunning() {
        mWriteThread.interrupt();
        mWriteThread = null;

        mReadThread.interrupt();
        mReadThread = null;

        if (mAcceptThread != null) {
            mAcceptThread.interrupt();
            mAcceptThread = null;
        }
    }
}