package com.flash3388.flashlib.net.hfcs;

import java.io.DataInput;
import java.io.IOException;

public interface InType<T> extends Type {

    Class<T> getClassType();

    T readFrom(DataInput input) throws IOException;
}
