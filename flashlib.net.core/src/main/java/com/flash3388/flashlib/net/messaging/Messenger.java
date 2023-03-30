package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;

public interface Messenger {

    RegisteredListener addListener(MessageListener listener);
    RegisteredListener addListener(MessageListener listener, MessageType type);

    void send(Message message);
}
