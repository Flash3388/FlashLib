package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.io.IoInterface;

public enum IoBackend {
    STUB {
        @Override
        IoInterface createInterface(FlashLibControl control) {
            return new IoInterface.Stub();
        }
    }
    ;

    abstract IoInterface createInterface(FlashLibControl control);
}
