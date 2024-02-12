package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.messaging.Message;

class MessagingChannelListener implements MessagingChannel.Listener {

    private final HfcsContext mContext;

    MessagingChannelListener(HfcsContext context) {
        mContext = context;
    }

    @Override
    public void onConnect() {
        mContext.markedConnected();
    }

    @Override
    public void onDisconnect() {
        mContext.markNotConnected();
    }

    @Override
    public void onNewMessage(MessageHeader header, Message message) {
        if (message.getType().getKey() != HfcsMessageType.KEY) {
            return;
        }

        HfcsUpdateMessage updateMessage = (HfcsUpdateMessage) message;
        mContext.updateReceivedNewData(header, updateMessage);
    }

    @Override
    public void onMessageSendingFailed(Message message) {

    }
}
