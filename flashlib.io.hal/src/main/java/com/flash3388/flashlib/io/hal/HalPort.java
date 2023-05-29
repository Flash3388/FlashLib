package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.IoPort;
import hal.HALJNI;

import java.io.IOException;

public class HalPort implements IoPort {

    protected final long mEnv;
    protected final long mHandle;

    public HalPort(long env, long handle) {
        mEnv = env;
        mHandle = handle;
    }

    public HalPort(long env, String name, int type) {
        mEnv = env;
        mHandle = HALJNI.open(env, name, type);
    }

    @Override
    public void close() throws IOException {
        HALJNI.close(mEnv, mHandle);
    }
}
