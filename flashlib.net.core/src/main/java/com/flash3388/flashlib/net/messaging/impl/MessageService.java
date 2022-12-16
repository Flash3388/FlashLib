package com.flash3388.flashlib.net.messaging.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageHandler;
import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;
import com.notifier.Controllers;
import com.notifier.EventController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageService extends TerminalServiceBase implements MessageQueue, MessageHandler {

    private final MessagingChannel mChannel;
    private final EventController mEventController;
    private final BlockingQueue<Message> mMessageQueue;

    private Thread mWriteThread;
    private Thread mReadThread;

    MessageService(MessagingChannel channel, EventController eventController, BlockingQueue<Message> messageQueue) {
        mChannel = channel;
        mEventController = eventController;
        mMessageQueue = messageQueue;

        mWriteThread = null;
        mReadThread = null;
    }

    public MessageService(MessagingChannel channel, EventController eventController) {
        this(channel, eventController, new LinkedBlockingQueue<>());
    }

    public MessageService(MessagingChannel channel) {
        this(channel, Controllers.newSyncExecutionController());
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
                new WriteTask(mChannel, mEventController, mMessageQueue),
                "MessagingService-WriteTask");
        mWriteThread.setDaemon(true);

        mReadThread = new Thread(
                new ReadTask(mChannel, mEventController),
                "MessagingService-ReadTask");
        mReadThread.setDaemon(true);

        mWriteThread.start();
        mReadThread.start();
    }

    @Override
    protected void stopRunning() {
        mWriteThread.interrupt();
        mWriteThread = null;

        mReadThread.interrupt();
        mReadThread = null;
    }
}
