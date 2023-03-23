package com.flash3388.flashlib.net.hfcs;

import java.io.DataOutput;
import java.io.IOException;

public interface OutData {

    void writeInto(DataOutput output) throws IOException;
}
