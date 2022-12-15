package com.flash3388.flashlib.hmi.comm.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.hmi.comm.BasicMessage;
import com.flash3388.flashlib.hmi.comm.MessageListener;
import com.flash3388.flashlib.hmi.comm.MessageQueue;
import com.flash3388.flashlib.hmi.comm.MessageReceiver;
import com.flash3388.flashlib.hmi.comm.NewMessageEvent;
import com.flash3388.flashlib.hmi.comm.io.MessageChannel;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessagingService extends TerminalServiceBase implements MessageQueue, MessageReceiver {

    private final MessageChannel mChannel;
    private final EventController mEventController;
    private final BlockingQueue<BasicMessage> mMessageQueue;

    private Thread mRunningThread;

    public MessagingService(MessageChannel channel, EventController eventController) {
        mChannel = channel;
        mEventController = eventController;
        mMessageQueue = new LinkedBlockingQueue<>();

        mRunningThread = null;
    }

    @Override
    public void send(BasicMessage basicMessage) {
        mMessageQueue.add(basicMessage);
    }

    @Override
    public void addListener(MessageListener listener) {
        mEventController.registerListener(listener);
    }

    @Override
    protected void startRunning() throws ServiceException {
        Thread thread = new Thread(new Task(
                mChannel,
                mEventController,
                mMessageQueue
        ), "messaging-service");
        thread.setDaemon(true);
        thread.start();

        mRunningThread = thread;
    }

    @Override
    protected void stopRunning() {
        if (mRunningThread != null) {
            mRunningThread.interrupt();
            mRunningThread = null;
        }
    }

    private static class Task implements Runnable {

        private final MessageChannel mChannel;
        private final EventController mEventController;
        private final BlockingQueue<BasicMessage> mMessageQueue;

        private Task(MessageChannel channel,
                     EventController eventController,
                     BlockingQueue<BasicMessage> messageQueue) {
            mChannel = channel;
            mEventController = eventController;
            mMessageQueue = messageQueue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // start by sending what we can
                    BasicMessage message = null;
                    do {
                        message = mMessageQueue.poll(5, TimeUnit.MILLISECONDS);
                        if (message != null) {
                            try {
                                mChannel.writeMessage(message);
                            } catch (IOException e) {
                                // retry?
                            }
                        }
                    } while (message != null);


                    try {
                        message = mChannel.readMessage(Time.milliseconds(50));
                        mEventController.fire(
                                new NewMessageEvent(message),
                                NewMessageEvent.class,
                                MessageListener.class,
                                MessageListener::onNewMessage);
                    } catch (IOException e) {
                        // what to do?
                    }
                }
            } catch (InterruptedException e) {

            }
        }
    }
}
