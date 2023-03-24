package com.flash3388.flashlib.net.hfcs;

import java.io.DataOutput;
import java.io.IOException;

/**
 * An outgoing data in HFCS.
 *
 * @since FlashLib 3.2.0
 */
public interface OutData {

    void writeInto(DataOutput output) throws IOException;
}
