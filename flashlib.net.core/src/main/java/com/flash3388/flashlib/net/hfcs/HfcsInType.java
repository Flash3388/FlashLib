package com.flash3388.flashlib.net.hfcs;

import java.io.DataInput;
import java.io.IOException;

/**
 * Describing a type of incoming packets.
 *
 * @param <T> type of data (object).
 * @since FlashLib 3.2.0
 */
public interface HfcsInType<T> extends HfcsType {

    T readFrom(DataInput input) throws IOException;
}
