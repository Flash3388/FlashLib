package com.flash3388.flashlib.control;

public interface Invertable {

    void setInverted(boolean inverted);
    boolean isInverted();

    default void invert() {
        setInverted(!isInverted());
    }
}
