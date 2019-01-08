package edu.flash3388.flashlib.vision;

public interface ImagePipeline<T extends Image> {

    void process(T image);
}
