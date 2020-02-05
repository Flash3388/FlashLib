package com.flash3388.flashlib.robot.hid;

public class NoSuchAxisException extends RuntimeException {

    public NoSuchAxisException(int channel, int axis) {
        super(String.format("No axis %d for channel %d", axis, channel));
    }
}
