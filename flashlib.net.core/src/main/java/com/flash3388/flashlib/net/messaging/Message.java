package com.flash3388.flashlib.net.messaging;

public interface Message extends InMessage, OutMessage {

    MessageType getType();
}
