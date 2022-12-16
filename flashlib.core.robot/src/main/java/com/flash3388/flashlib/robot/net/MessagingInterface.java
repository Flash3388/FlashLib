package com.flash3388.flashlib.robot.net;

import com.flash3388.flashlib.net.messaging.MessageHandler;
import com.flash3388.flashlib.net.messaging.MessageQueue;
import com.flash3388.flashlib.net.messaging.MessageType;

public interface MessagingInterface {

    void registerMessageType(MessageType type);

    MessageQueue getQueue();
    MessageHandler getHandler();
}
