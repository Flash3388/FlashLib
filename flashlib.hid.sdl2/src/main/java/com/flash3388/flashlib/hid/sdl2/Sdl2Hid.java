package com.flash3388.flashlib.hid.sdl2;

import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;

import java.io.Closeable;

public class Sdl2Hid {

    private static final Sdl2HidData sHidData;
    private static Sdl2UpdateService sUpdateService;

    static {
        Sdl2Natives.loadNatives();

        sHidData = new Sdl2HidData();
    }

    public static Closeable initialize() {
        sUpdateService = new Sdl2UpdateService(sHidData);
        try {
            sUpdateService.start();
        } catch (ServiceException e) {
            throw new Error(e);
        }

        return ()-> {
            sUpdateService.close();
        };
    }

    public static Sdl2HidData getHidData() {
        return sHidData;
    }

    public static HidChannel newChannel(int channel) {
        return new GenericHidChannel(channel);
    }
}
