package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.packing.DataBufferPacker;
import edu.flash3388.flashlib.io.packing.DataBufferUnpacker;
import edu.flash3388.flashlib.io.packing.Packing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscoveryDataMessage implements Message {

    public static final int HEADER = 7002;

    private final State mState;
    private final Collection<SendableData> mSendables;

    public DiscoveryDataMessage(State state, Collection<SendableData> sendables) {
        mState = state;
        mSendables = sendables;
    }

    @Override
    public int getHeader() {
        return HEADER;
    }

    @Override
    public byte[] getData() {
        try {
            DataBufferPacker packer = Packing.newBufferPacker();

            packer.packInt(mState.getValue());
            packer.packInt(mSendables.size());

            for (SendableData sendableData : mSendables) {
                 sendableData.pack(packer);
            }

            packer.toByteArray();
            return packer.toByteArray();
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

    public static DiscoveryDataMessage fromMessage(Message message) {
        byte[] allData = message.getData();

        DataBufferUnpacker unpacker = Packing.newBufferUnpacker(allData);

        try {
            int stateValue = unpacker.unpackInt();
            State state = State.fromValue(stateValue);

            int count = unpacker.unpackInt();

            List<SendableData> sendableDataList = new ArrayList<SendableData>();
            for (int i = 0; i < count; i++) {
                SendableData sendableData = SendableData.unpackObject(unpacker);
                sendableDataList.add(sendableData);
            }

            return new DiscoveryDataMessage(state, sendableDataList);
        } catch (IOException e) {
            // shouldn't occur
            throw new RuntimeException(e);
        }
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
