package com.flash3388.flashlib.io;

import java.io.DataOutput;
import java.io.IOException;

public interface Serializable {

    void writeInto(DataOutput output) throws IOException;
}
