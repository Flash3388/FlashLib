package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.IoChannel;

public class HalIoChannel implements IoChannel {

    private final String mName;

    public HalIoChannel(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
