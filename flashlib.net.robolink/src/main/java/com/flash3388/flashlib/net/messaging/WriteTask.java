package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.OutMessagingChannel;
import com.flash3388.flashlib.net.messaging.Message;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

class WriteTask implements Runnable {

    private final OutMessagingChannel mChannel;
    private final BlockingQueue<Message> mQueue;
    private final Logger mLogger;

    WriteTask(OutMessagingChannel channel,
              BlockingQueue<Message> queue,
              Logger logger) {
        mChannel = channel;
        mQueue = queue;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message message = mQueue.take();
                boolean isOnlyForServer = message.getType().equals(PingMessage.TYPE);

                mChannel.write(message, isOnlyForServer);
            } catch (IOException e) {
                mLogger.debug("WriteTask: Error writing message", e);
                // TODO: WE CAN REQUEUE MESSAGE, FOR A FEW ATTEMPTS
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
                mLogger.debug("WriteTask: Unknown error", t);
            }
        }
    }
}
