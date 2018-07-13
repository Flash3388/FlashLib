package edu.flash3388.flashlib.communications.message.event;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.event.Event;

public class MessageEvent implements Event {

    private Message mMessage;
    private MessageWriter mWriter;

    public MessageEvent(Message message, MessageWriter writer) {
        mMessage = message;
        mWriter = writer;
    }

    public Message getMessage() {
        return mMessage;
    }

    public void writeResponse(Message message) throws WriteException {
        mWriter.writeMessage(message);
    }
}
