package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.hid.sdl2.Sdl2HidInterface;
import com.flash3388.flashlib.robot.hfcs.hid.HfcsHid;

public enum HidBackend {
    STUB {
        @Override
        HidInterface createInterface(FlashLibControl control) {
            return new HidInterface.Stub();
        }
    },
    SDL2 {
        @Override
        HidInterface createInterface(FlashLibControl control) {
            return new WeakHidInterface(new Sdl2HidInterface(), control.getMainThread());
        }
    },
    HFCS {
        @Override
        HidInterface createInterface(FlashLibControl control) {
            assert control.getNetworkInterface().getMode().isHfcsEnabled();
            return HfcsHid.createReceiver(control.getNetworkInterface().getHfcsRegistry(), control.getMainThread());
        }
    }
    ;

    abstract HidInterface createInterface(FlashLibControl control);
}
