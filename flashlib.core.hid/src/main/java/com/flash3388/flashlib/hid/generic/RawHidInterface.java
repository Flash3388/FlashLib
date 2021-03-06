package com.flash3388.flashlib.hid.generic;

public interface RawHidInterface {

    int NO_HID_CHANNEL = -1;

    boolean hasChannel(int channel);
    ChannelType getChannelType(int channel);

    int getAxesCount(int channel);
    int getButtonsCount(int channel);
    int getPovsCount(int channel);

    double getAxisValue(int channel, int axis);
    boolean getButtonValue(int channel, int button);
    int getPovValue(int channel, int pov);
}
