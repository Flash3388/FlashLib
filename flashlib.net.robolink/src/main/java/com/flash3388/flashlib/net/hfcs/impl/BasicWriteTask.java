package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.HfcsOutMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class BasicWriteTask implements Runnable {

    private final BlockingQueue<OutDataNode> mDataQueue;
    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessageType mMessageType;

    public BasicWriteTask(BlockingQueue<OutDataNode> dataQueue, MessagingChannel channel, Logger logger,
                          MessageType messageType) {
        mDataQueue = dataQueue;
        mChannel = channel;
        mLogger = logger;
        mMessageType = messageType;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                OutDataNode node = mDataQueue.take();
                Type type = node.getType();
                OutData data = node.getData();
                node.updateSent();
                mDataQueue.add(node);

                mLogger.debug("Sending data of type {}", type.getKey());
                mChannel.write(mMessageType, new HfcsOutMessage(type, data));
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                mLogger.error("Error while sending data", e);
            }
        }
    }
}
