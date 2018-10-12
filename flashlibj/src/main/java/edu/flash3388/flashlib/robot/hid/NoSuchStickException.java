package edu.flash3388.flashlib.robot.hid;

public class NoSuchStickException extends RuntimeException {

    public NoSuchStickException(int channel, int stick) {
        super(String.format("No stick %d for channel %d", stick, channel));
    }
}
