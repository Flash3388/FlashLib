package com.flash3388.flashlib.hid.sdl2;

import com.flash3388.flashlib.hid.generic.ChannelType;
import com.flash3388.flashlib.hid.generic.RawHidInterface;
import sdl2.JoystickType;
import sdl2.SDLJoystick;

public class Sdl2HidInterface implements RawHidInterface {

    private final Sdl2HidData mHidData;

    public Sdl2HidInterface(Sdl2HidData hidData) {
        mHidData = hidData;
    }

    public Sdl2HidInterface() {
        this(Sdl2Hid.getHidData());
    }

    @Override
    public boolean hasChannel(int channel) {
        return mHidData.isConnected(channel);
    }

    @Override
    public ChannelType getChannelType(int channel) {
        if (!hasChannel(channel)) {
            return ChannelType.HID;
        }

        JoystickType type = mHidData.getMeta(channel).getType();
        switch (type) {
            case GAME_CONTROLLER:
                return ChannelType.XBOX;
            case FLIGHT_STICK:
                return ChannelType.JOYSTICK;
            default:
                return ChannelType.HID;
        }
    }

    @Override
    public int getAxesCount(int channel) {
        if (!hasChannel(channel)) {
            return 0;
        }

        return mHidData.getMeta(channel).getNumAxes();
    }

    @Override
    public int getButtonsCount(int channel) {
        if (!hasChannel(channel)) {
            return 0;
        }

        return mHidData.getMeta(channel).getNumButtons();
    }

    @Override
    public int getPovsCount(int channel) {
        if (!hasChannel(channel)) {
            return 0;
        }

        return mHidData.getMeta(channel).getNumHats();
    }

    @Override
    public double getAxisValue(int channel, int axis) {
        if (!hasChannel(channel)) {
            return 0;
        }

        return (double) mHidData.getAxis(channel, axis) / SDLJoystick.AXIS_MAX;
    }

    @Override
    public boolean getButtonValue(int channel, int button) {
        if (!hasChannel(channel)) {
            return false;
        }

        return mHidData.getButton(channel, button);
    }

    @Override
    public int getPovValue(int channel, int pov) {
        if (!hasChannel(channel)) {
            return 0;
        }

        int rawHat = mHidData.getHat(channel, pov);
        if (rawHat == SDLJoystick.HAT_CENTERED) {
            return -1;
        }

        if ((rawHat & SDLJoystick.HAT_UP) != 0) {
            if ((rawHat & SDLJoystick.HAT_RIGHT) != 0) {
                return 45;
            } else if ((rawHat & SDLJoystick.HAT_LEFT) != 0) {
                return 315;
            }
        } else if ((rawHat & SDLJoystick.HAT_DOWN) != 0) {
            if ((rawHat & SDLJoystick.HAT_RIGHT) != 0) {
                return 135;
            } else if ((rawHat & SDLJoystick.HAT_LEFT) != 0) {
                return 225;
            }
        } else if ((rawHat & SDLJoystick.HAT_RIGHT) != 0) {
            return 90;
        } else if ((rawHat & SDLJoystick.HAT_LEFT) != 0) {
            return 270;
        }

        return 0;
    }
}
