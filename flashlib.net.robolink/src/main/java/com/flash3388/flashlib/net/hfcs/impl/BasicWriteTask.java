package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.DataMessage;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.hfcs.messages.OutPackage;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class BasicWriteTask implements Runnable {

    private static final MessageType MESSAGE_TYPE = new DataMessageType();

    private final BlockingQueue<OutDataNode> mDataQueue;
    private final WritableMessagingChannel mChannel;
    private final Logger mLogger;

    public BasicWriteTask(BlockingQueue<OutDataNode> dataQueue, WritableMessagingChannel channel, Logger logger) {
        mDataQueue = dataQueue;
        mChannel = channel;
        mLogger = logger;
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
                mChannel.write(MESSAGE_TYPE, new DataMessage(new OutPackage(type, data)));
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                mLogger.error("Error while sending data", e);
            }
        }
    }
}
