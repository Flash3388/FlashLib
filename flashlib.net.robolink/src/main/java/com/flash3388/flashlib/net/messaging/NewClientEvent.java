package com.flash3388.flashlib.net.messaging;

import com.notifier.Event;

public class NewClientEvent implements Event {

    private final ChannelId mClientId;

    public NewClientEvent(ChannelId clientId) {
        mClientId = clientId;
    }

    public ChannelId getClientId() {
        return mClientId;
    }
}
