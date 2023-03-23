package com.flash3388.flashlib.net.hfcs;

import com.notifier.Listener;

public interface DataListener<T> extends Listener {

    void onReceived(DataReceivedEvent<T> event);
}
