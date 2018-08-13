package edu.flash3388.flashlib.communications.message.event;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.event.Event;

public class MessageEvent implements Event {

    private Message mMessage;
    private MessageQueue mOutputMessageQueue;

    public MessageEvent(Message message, MessageQueue outputMessageQueue) {
        mMessage = message;
        mOutputMessageQueue = outputMessageQueue;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void writeResponse(Message message) {
        mOutputMessageQueue.enqueueMessage(message);
    }
}
