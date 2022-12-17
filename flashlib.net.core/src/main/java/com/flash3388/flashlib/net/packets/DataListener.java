package com.flash3388.flashlib.net.packets;

import com.notifier.Listener;

public interface DataListener extends Listener {

    void onNewPacketReceived(NewPacketEvent event);
}
