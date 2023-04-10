package com.flash3388.flashlib.vision.control;

public class StandardVisionOptions {

    private StandardVisionOptions() {}

    public static final VisionOption<Boolean> DEBUG = VisionOption.create("debug", Boolean.class);
    public static final VisionOption<Integer> EXPOSURE = VisionOption.create("exposure", Integer.class);

    public static void fill(KnownVisionOptions optionTypes) {
        optionTypes.put(DEBUG);
        optionTypes.put(EXPOSURE);
    }
}
