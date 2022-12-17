package com.flash3388.flashlib.net.messaging.io;

import com.castle.util.function.ThrowingConsumer;
import com.castle.util.function.ThrowingFunction;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.flash3388.flashlib.net.messaging.io.IoUtils.deserialize;
import static com.flash3388.flashlib.net.messaging.io.IoUtils.serialize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class MessageHeaderTest {


    @Test
    public void ctor_parseForDataInput_correctVersionAndContent() throws Exception {
        final int MESSAGE_TYPE = 341;
        byte[] bytes = serialize((output) -> {
            output.writeInt(MessageHeader.VERSION);
            output.writeInt(MESSAGE_TYPE);
        });

        MessageHeader header = deserialize(bytes, MessageHeader::new);

        assertThat(header.getMessageType(), equalTo(MESSAGE_TYPE));
    }

    @Test
    public void ctor_parseForDataInput_wrongVersion() throws Exception {
        final int MESSAGE_TYPE = 341;
        byte[] bytes = serialize((output) -> {
            output.writeInt(MessageHeader.VERSION + 20);
            output.writeInt(MESSAGE_TYPE);
        });

        assertThrows(IOException.class, ()-> {
            deserialize(bytes, MessageHeader::new);
        });
    }

    @Test
    public void ctor_parseForDataInput_missingData() throws Exception {
        byte[] bytes = serialize((output) -> {
            output.writeInt(MessageHeader.VERSION);
        });

        assertThrows(IOException.class, ()-> {
            deserialize(bytes, MessageHeader::new);
        });
    }

    @Test
    public void writeTo_writesData_asExpected() throws Exception {
        final int MESSAGE_TYPE = 341;
        MessageHeader header = new MessageHeader(MESSAGE_TYPE);

        byte[] bytes = serialize(header::writeTo);

        deserialize(bytes, (input) -> {
            int version = input.readInt();
            assertThat(version, equalTo(MessageHeader.VERSION));
            int messageType = input.readInt();
            assertThat(messageType, equalTo(MESSAGE_TYPE));

            return null;
        });
    }
}