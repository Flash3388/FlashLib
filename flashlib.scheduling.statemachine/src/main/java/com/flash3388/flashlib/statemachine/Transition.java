package com.flash3388.flashlib.statemachine;

import java.util.function.BooleanSupplier;

public interface Transition {

    Transition on(BooleanSupplier condition);

    void initiate();
}
