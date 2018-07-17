package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableStream;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class SendableMessageWriter implements SendableStream {

    private MessageWriter mMessageWriter;
    private SendableData mSendableData;
    private SendableData mRemoteSendableData;
    private PrimitiveSerializer mSerializer;

    SendableMessageWriter(MessageWriter messageWriter, SendableData sendableData, SendableData remoteSendableData, PrimitiveSerializer serializer) {
        mMessageWriter = messageWriter;
        mSendableData = sendableData;
        mRemoteSendableData = remoteSendableData;
        mSerializer = serializer;
    }

    @Override
    public void sendMessage(Message message) throws WriteException {
        Message sendableMessage = new SendableMessage(
                SendableMessageHeader.SENDABLE_MESSAGE_HEADER,
                mSendableData, mRemoteSendableData,
                message, mSerializer);

        mMessageWriter.writeMessage(sendableMessage);
    }
}
