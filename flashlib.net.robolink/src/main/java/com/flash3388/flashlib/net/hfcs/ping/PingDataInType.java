package com.flash3388.flashlib.net.hfcs.ping;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.IOException;

public class PingDataInType extends PingDataType implements InType<PingData> {

    @Override
    public Class<PingData> getClassType() {
        return PingData.class;
    }

    @Override
    public PingData readFrom(DataInput input) throws IOException {
        long timeMillis = input.readLong();
        Time clockTime = Time.milliseconds(timeMillis);
        return new PingData(clockTime);
    }
}
