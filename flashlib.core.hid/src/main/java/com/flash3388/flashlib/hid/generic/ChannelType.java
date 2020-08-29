package com.flash3388.flashlib.hid.generic;

public enum ChannelType {
    AXIS,
    BUTTON,
    POV,
    HID {
        @Override
        public boolean doesSupport(ChannelType other) {
            return other == HID || other == XBOX || other == JOYSTICK;
        }
    },
    XBOX,
    JOYSTICK
    ;

    public boolean doesSupport(ChannelType other) {
        return this == other;
    }
}
