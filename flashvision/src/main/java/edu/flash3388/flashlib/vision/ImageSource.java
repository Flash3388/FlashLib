package edu.flash3388.flashlib.vision;

import java.util.Optional;
import java.util.function.Supplier;

public interface ImageSource<T extends Image> extends Supplier<Optional<T>> {

    @Override
    Optional<T> get();
}
