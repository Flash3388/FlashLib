package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import org.slf4j.Logger;

import java.io.IOException;

public class StorageListenerImpl implements StorageListener {

    // TODO: USE MESSAGE QUEUE? use them to retry changes

    private final WritableMessagingChannel mChannel;
    private final Logger mLogger;

    public StorageListenerImpl(WritableMessagingChannel channel, Logger logger) {
        mChannel = channel;
        mLogger = logger;
    }

    @Override
    public void onNewEntry(StoragePath path) {
        try {
            mChannel.write(NewEntryMessage.TYPE, new NewEntryMessage(path.toString()));
        } catch (IOException e) {
            mLogger.debug("error writing message from storage", e);
        } catch (InterruptedException e) {
            // we don't care about this
        }
    }

    @Override
    public void onEntryUpdate(StoragePath path, Value value) {
        try {
            mChannel.write(EntryChangeMessage.TYPE, new EntryChangeMessage(path.toString(), value));
        } catch (IOException e) {
            mLogger.debug("error writing message from storage", e);
        } catch (InterruptedException e) {
            // we don't care about this
        }
    }

    @Override
    public void onEntryClear(StoragePath path) {
        try {
            mChannel.write(EntryClearMessage.TYPE, new EntryClearMessage(path.toString()));
        } catch (IOException e) {
            mLogger.debug("error writing message from storage", e);
        } catch (InterruptedException e) {
            // we don't care about this
        }
    }
}
