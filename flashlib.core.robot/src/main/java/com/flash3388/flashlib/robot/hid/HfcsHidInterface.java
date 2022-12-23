package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.hid.generic.ChannelType;
import com.flash3388.flashlib.hid.generic.RawHidInterface;

public class HfcsHidInterface implements RawHidInterface {

    private final HidData mHidData;

    public HfcsHidInterface(HidData hidData) {
        mHidData = hidData;
    }

    @Override
    public boolean hasChannel(int channel) {
        return mHidData.hasChannel(channel);
    }

    @Override
    public ChannelType getChannelType(int channel) {
        return mHidData.getChannelType(channel);
    }

    @Override
    public int getAxesCount(int channel) {
        return mHidData.getAxesCount(channel);
    }

    @Override
    public int getButtonsCount(int channel) {
        return mHidData.getButtonCount(channel);
    }

    @Override
    public int getPovsCount(int channel) {
        return mHidData.getPovCount(channel);
    }

    @Override
    public double getAxisValue(int channel, int axis) {
        return mHidData.getAxisValue(channel, axis);
    }

    @Override
    public boolean getButtonValue(int channel, int button) {
        return mHidData.getButtonValue(channel, button);
    }

    @Override
    public int getPovValue(int channel, int pov) {
        return mHidData.getPovValue(channel, pov);
    }
}
