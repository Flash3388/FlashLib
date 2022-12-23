package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.IOException;

public interface MessageReader {

    class Result {
        public final InstanceId senderId;
        public final MessageType type;
        public final Message message;

        public Result(InstanceId senderId, MessageType type, Message message) {
            this.senderId = senderId;
            this.type = type;
            this.message = message;
        }
    }

    Result read(DataInput dataInput) throws IOException;
}
