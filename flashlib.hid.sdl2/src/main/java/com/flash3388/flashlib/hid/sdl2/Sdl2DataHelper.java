package com.flash3388.flashlib.hid.sdl2;

import com.flash3388.flashlib.hid.generic.ChannelType;
import sdl2.JoystickType;
import sdl2.SDLJoystick;

public class Sdl2DataHelper {

    private Sdl2DataHelper() {}

    public static double axisValue(int value) {
        return (double) value / SDLJoystick.AXIS_MAX;
    }

    public static int hatValue(int rawHat) {
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

    public static ChannelType channelType(JoystickType type) {
        switch (type) {
            case GAME_CONTROLLER:
                return ChannelType.XBOX;
            case FLIGHT_STICK:
                return ChannelType.JOYSTICK;
            default:
                return ChannelType.HID;
        }
    }
}
