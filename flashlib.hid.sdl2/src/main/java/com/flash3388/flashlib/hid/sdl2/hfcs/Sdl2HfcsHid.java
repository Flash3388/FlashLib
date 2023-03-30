package com.flash3388.flashlib.hid.sdl2.hfcs;

import com.castle.concurrent.service.TerminalService;
import com.flash3388.flashlib.hid.sdl2.Sdl2Natives;
import com.flash3388.flashlib.robot.hfcs.hid.HidData;

public class Sdl2HfcsHid {

    private Sdl2HfcsHid() {}

    public static TerminalService initialize(HidData hidData) {
        Sdl2Natives.loadNatives();

        return new Sdl2UpdateService(hidData);
    }
}
