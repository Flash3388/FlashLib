package edu.flash3388.flashlib.io.packing;

import java.io.IOException;

public interface Packable {
    void pack(DataPacker packer) throws IOException;
    void unpack(DataUnpacker unpacker) throws IOException;
}
