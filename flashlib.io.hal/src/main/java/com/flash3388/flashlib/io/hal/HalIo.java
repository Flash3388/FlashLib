package com.flash3388.flashlib.io.hal;

import com.castle.code.Natives;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.flash3388.flashlib.io.IoChannel;
import hal.HALJNI;

import java.io.Closeable;
import java.io.IOException;

public class HalIo {

    private HalIo() {}

    static {
        try {
            Natives.newLoader().load("libhal_jni");
        } catch (FindException | CodeLoadException | IOException e) {
            throw new Error(e);
        }
    }

    private static long sEnv;

    static long getEnv() {
        return sEnv;
    }

    public static synchronized Closeable initialize() {
        sEnv = HALJNI.init();
        return ()-> {
            synchronized (HalIo.class) {
                if (sEnv != 0) {
                    HALJNI.shutdown(sEnv);
                    sEnv = 0;
                }
            }
        };
    }

    public static IoChannel newChannel(String name) {
        return new HalIoChannel(name);
    }
}
