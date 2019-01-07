package edu.flash3388.flashlib.vision;

import java.util.function.Supplier;

public interface ImageSource extends Supplier<Image> {

    @Override
    Image get();
}
