package edu.flash3388.flashlib.vision;

import java.util.Optional;
import java.util.function.Supplier;

public interface ImageSource extends Supplier<Optional<Image>> {

    @Override
    Optional<Image> get();
}
