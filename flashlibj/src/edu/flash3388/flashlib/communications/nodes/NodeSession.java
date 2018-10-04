package edu.flash3388.flashlib.communications.nodes;

import edu.flash3388.flashlib.communications.message.Message;

import java.io.Closeable;

public interface NodeSession extends Closeable {

    void onMessageReceived(Message message);

    @Override
    void close();
}
