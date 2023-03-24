package com.flash3388.flashlib.net.hfcs;

import java.io.DataInput;
import java.io.IOException;

/**
 * Describing a type of incoming packets.
 *
 * @param <T> type of data (object).
 * @since FlashLib 3.2.0
 */
public interface InType<T> extends Type {

    Class<T> getClassType();

    T readFrom(DataInput input) throws IOException;
}
