package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import org.slf4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.NoSuchElementException;

class HfcsMessageType implements MessageType {

    public static final int KEY = 1111;

    private final KnownInDataTypes mInDataTypes;
    private final Logger mLogger;

    HfcsMessageType(KnownInDataTypes inDataTypes, Logger logger) {
        mInDataTypes = inDataTypes;
        mLogger = logger;
    }

    @Override
    public int getKey() {
        return KEY;
    }

    @Override
    public Message read(DataInput dataInput) throws IOException {
        try {
            int typeKey = dataInput.readInt();
            HfcsInType<?> type = mInDataTypes.get(typeKey);
            Object data = type.readFrom(dataInput);

            return new HfcsUpdateMessage(this, type, data);
        } catch (NoSuchElementException e) {
            // ignore this message and inform
            mLogger.warn("Received unknown hfcs data. Ignoring it");
            return null;
        }
    }

    @Override
    public void write(Message message, DataOutput dataOutput) throws IOException {
        HfcsUpdateMessage updateMessage = (HfcsUpdateMessage) message;
        dataOutput.writeInt(updateMessage.getHfcsType().getKey());

        Object data = updateMessage.getData();
        // this cheat works because higher interfaces ensure that
        // data used by hfcs out is serializable
        // but it would be best to find a different way
        // so that this will be enforced by java
        ((Serializable)data).writeInto(dataOutput);
    }
}
