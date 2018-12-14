package edu.flash3388.flashlib.robot.control;

public interface Invertable {

    void setInverted(boolean inverted);
    boolean isInverted();

    default void invert() {
        setInverted(!isInverted());
    }
}
