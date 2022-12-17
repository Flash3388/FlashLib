package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.Remote;
import com.notifier.Event;

public class ConnectionEvent implements Event {

    private final Remote mRemote;

    public ConnectionEvent(Remote remote) {
        mRemote = remote;
    }

    public Remote getRemote() {
        return mRemote;
    }
}
