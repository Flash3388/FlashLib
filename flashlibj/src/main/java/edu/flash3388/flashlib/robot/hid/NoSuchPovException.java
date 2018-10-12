package edu.flash3388.flashlib.robot.hid;

public class NoSuchPovException extends RuntimeException {

    public NoSuchPovException(int channel, int pov) {
        super(String.format("No Pov %d for channel %d", pov, channel));
    }
}
