package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.Remote;
import com.flash3388.flashlib.time.Time;

public class MessageInfoImpl implements MessageInfo {

    private final Remote mRemote;
    private final Time mTimestamp;

    public MessageInfoImpl(Remote remote, Time timestamp) {
        mRemote = remote;
        mTimestamp = timestamp;
    }

    @Override
    public Remote getSender() {
        return mRemote;
    }

    @Override
    public Time getTimestamp() {
        return mTimestamp;
    }
}
