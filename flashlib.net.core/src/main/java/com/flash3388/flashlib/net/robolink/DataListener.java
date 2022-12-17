package com.flash3388.flashlib.net.robolink;

import com.notifier.Listener;

public interface DataListener extends Listener {

    void onNewPacketReceived(NewPacketEvent event);
}
