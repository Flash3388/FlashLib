package com.flash3388.flashlib.hid;

import java.util.Arrays;

public interface Dpad extends Pov {

    Button up();
    Button down();
    Button left();
    Button right();

    default Iterable<Button> buttons() {
        return Arrays.asList(up(), down(), left(), right());
    }
}
