package edu.flash3388.flashlib.robot.hid;

public class NoSuchButtonException extends RuntimeException {

    public NoSuchButtonException(int channel, int button) {
        super(String.format("No button %d for channel %d", button, channel));
    }
}
