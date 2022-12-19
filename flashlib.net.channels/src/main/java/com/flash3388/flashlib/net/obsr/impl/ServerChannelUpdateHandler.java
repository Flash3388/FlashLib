package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.ServerMessagingChannel;
import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import com.flash3388.flashlib.net.obsr.BasicEntry;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ServerChannelUpdateHandler extends ChannelUpdateHandler implements ServerMessagingChannel.UpdateHandler {

    private final WritableMessagingChannel mChannel;

    public ServerChannelUpdateHandler(Storage storage, Logger logger, WritableMessagingChannel channel) {
        super(storage, logger);
        mChannel = channel;
    }

    @Override
    public Optional<Message> onNewClientSend() {
        Map<String, BasicEntry> entries = mStorage.getAll();
        return Optional.of(new StorageContentsMessage(entries));
    }

    @Override
    public void onNewMessage(MessageInfo messageInfo, Message message) {
        super.onNewMessage(messageInfo, message);

        try {
            mChannel.write(message);
        } catch (IOException e) {
            mLogger.debug("Error transferring message to other clients", e);
        } catch (InterruptedException e) {
            // interrupt our thread
            Thread.currentThread().interrupt();
        }
    }
}
