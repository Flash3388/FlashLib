package com.flash3388.flashlib.vision;

public interface ImageSource<T extends Image> {

    T get() throws VisionException;
}
