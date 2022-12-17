package com.flash3388.flashlib.net.robolink;

import com.flash3388.flashlib.net.robolink.Remote;
import com.flash3388.flashlib.time.Time;

import java.util.concurrent.atomic.AtomicReference;

public class RemoteImpl implements Remote {

    private final String mId;
    private final AtomicReference<Time> mLastSeen;

    public RemoteImpl(String id) {
        mId = id;
        mLastSeen = new AtomicReference<>(Time.INVALID);
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public Time getLastSeenTimestamp() {
        return mLastSeen.get();
    }

    public void updateLastSeen(Time time) {
        mLastSeen.set(time);
    }
}
