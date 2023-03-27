package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessageToSend;
import com.flash3388.flashlib.net.message.ServerMessagingChannel;
import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ServerChannelUpdateHandler extends ChannelUpdateHandler implements ServerMessagingChannel.UpdateHandler {

    private final WritableMessagingChannel mChannel;

    public ServerChannelUpdateHandler(Storage storage, Logger logger, WritableMessagingChannel channel) {
        super(storage, logger, (type, msg)-> {
            try {
                channel.write(type, msg);
            } catch (IOException e) {
                logger.debug("Error writing message", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        mChannel = channel;
    }

    @Override
    public Optional<MessageToSend> onNewClientSend() {
        Map<String, Value> entries = mStorage.getAll();
        return Optional.of(new MessageToSend(
                StorageContentsMessage.TYPE,
                new StorageContentsMessage(entries)
        ));
    }

    @Override
    public void onNewMessage(MessageInfo messageInfo, Message message) {
        super.onNewMessage(messageInfo, message);

        try {
            mChannel.write(messageInfo.getType(), message);
        } catch (IOException e) {
            mLogger.error("Error transferring message to other clients", e);
        } catch (InterruptedException e) {
            // interrupt our thread
            Thread.currentThread().interrupt();
        }
    }
}
