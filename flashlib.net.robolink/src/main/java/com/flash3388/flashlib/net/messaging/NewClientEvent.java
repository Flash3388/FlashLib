package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Event;

public class NewClientEvent implements Event {

    private final InstanceId mClientId;

    public NewClientEvent(InstanceId clientId) {
        mClientId = clientId;
    }

    public InstanceId getClientId() {
        return mClientId;
    }
}
