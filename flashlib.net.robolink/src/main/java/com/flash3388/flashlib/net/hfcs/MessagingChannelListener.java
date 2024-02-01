package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.messaging.Message;

public class MessagingChannelListener implements MessagingChannel.Listener {

    @Override
    public void onConnect() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {

    }

    @Override
    public void onMessageSendingFailed(Message message) {

    }
}
