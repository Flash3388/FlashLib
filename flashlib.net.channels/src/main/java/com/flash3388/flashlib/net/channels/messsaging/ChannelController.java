package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.messaging.Message;

public interface ChannelController {

    NetChannel getChannel();

    SendRequest getNextSendRequest();

    void resetChannel();

    void onNewMessage(NetAddress address, MessageHeader header, Message message);
    void onMessageSendingFailed(Message message, Throwable cause);
    void onChannelCustomUpdate(Object param);
    void onChannelConnectable();
}
