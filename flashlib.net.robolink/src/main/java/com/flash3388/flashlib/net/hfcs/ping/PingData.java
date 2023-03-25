package com.flash3388.flashlib.net.hfcs.ping;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.time.Time;

import java.io.DataOutput;
import java.io.IOException;

public class PingData implements OutData {

    private final Time mClockTime;

    public PingData(Time clockTime) {
        mClockTime = clockTime;
    }

    public Time getClockTime() {
        return mClockTime;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeLong(mClockTime.valueAsMillis());
    }
}
