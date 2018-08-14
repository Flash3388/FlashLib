package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DiscoveryDataMessage implements Message {

    public static final int HEADER = 7002;

    private final State mState;
    private final Collection<SendableData> mSendables;
    private final PrimitiveSerializer mSerializer;

    public DiscoveryDataMessage(State state, Collection<SendableData> sendables, PrimitiveSerializer serializer) {
        mState = state;
        mSendables = sendables;
        mSerializer = serializer;
    }

    @Override
    public int getHeader() {
        return HEADER;
    }

    @Override
    public byte[] getData() {
        try {
            byte[] state = mSerializer.toBytes(mState.getValue());
            byte[] sendablesCount = mSerializer.toBytes(mSendables.size());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(state);
            outputStream.write(sendablesCount);

            for (SendableData sendableData : mSendables) {
                 byte[] sendableSerializedData = sendableData.toBytes(mSerializer);
                 outputStream.write(sendableSerializedData);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    public Collection<SendableData> getSendables() {
        return mSendables;
    }

    public State getState() {
        return mState;
    }

    public static DiscoveryDataMessage fromMessage(Message message, PrimitiveSerializer serializer) {
        byte[] allData = message.getData();

        int stateValue = serializer.toInt(allData);
        State state = State.fromValue(stateValue);

        int count = serializer.toInt(allData, 4);

        List<SendableData> sendableDataList = new ArrayList<SendableData>();
        int offset = 8;
        for (int i = 0; i < count; i++) {
            byte[] data = Arrays.copyOfRange(allData, offset, SendableData.getSerializedSize());
            SendableData sendableData = SendableData.fromBytes(data, serializer);
            sendableDataList.add(sendableData);
        }

        return new DiscoveryDataMessage(state, sendableDataList, serializer);
    }

    public enum State {
        ATTACHED(0), DETACHED(1);

        private final int mValue;

        State(int value) {
            mValue = value;
        }

        int getValue() {
            return mValue;
        }

        static State fromValue(int value) {
            for (State state : values()) {
                if (state.getValue() == value) {
                    return state;
                }
            }

            throw new EnumConstantNotPresentException(State.class, String.valueOf(value));
        }
    }
}
