package com.flash3388.flashlib.hid.sdl2;

import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;

public class Sdl2Hid {

    private static final Sdl2HidData sHidData;
    private static final Sdl2UpdateService sUpdateService;

    static {
        Sdl2Natives.loadNatives();

        sHidData = new Sdl2HidData();
        sUpdateService = new Sdl2UpdateService(sHidData);
        try {
            sUpdateService.start();
        } catch (ServiceException e) {
            throw new Error(e);
        }
    }

    public static Sdl2HidData getHidData() {
        return sHidData;
    }

    public static HidChannel newChannel(int channel) {
        return new GenericHidChannel(channel);
    }
}
