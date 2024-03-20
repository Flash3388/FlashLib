package com.flash3388.flashlib.net.channels.nio;

import com.notifier.Listener;

import java.nio.channels.SelectionKey;

public interface ChannelListener extends Listener {

    void onAcceptable(SelectionKey key);
    void onConnectable(SelectionKey key);
    void onReadable(SelectionKey key);
    void onWritable(SelectionKey key);

    void onRequestedUpdate(SelectionKey key, Object param);
}
