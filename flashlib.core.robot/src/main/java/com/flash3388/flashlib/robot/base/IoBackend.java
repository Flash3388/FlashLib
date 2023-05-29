package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.hal.HalIo;
import com.flash3388.flashlib.io.hal.HalIoInterface;

public enum IoBackend {
    STUB {
        @Override
        IoInterface createInterface(FlashLibControl control) {
            return new IoInterface.Stub();
        }
    },
    HAL {
        @Override
        IoInterface createInterface(FlashLibControl control) {
            control.registerCloseables(HalIo.initialize());
            return new HalIoInterface();
        }
    }
    ;

    abstract IoInterface createInterface(FlashLibControl control);
}
