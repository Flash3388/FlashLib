package com.flash3388.flashlib.hid.sdl2.hfcs;

import com.castle.concurrent.service.TerminalService;
import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.hid.sdl2.Sdl2Natives;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;

public class Sdl2HfcsHid {

    private Sdl2HfcsHid() {}

    public static TerminalService initialize(HidData hidData, NamedThreadFactory threadFactory) {
        Sdl2Natives.loadNatives();

        return new Sdl2UpdateService(hidData, threadFactory);
    }
}
