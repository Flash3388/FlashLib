package com.flash3388.flashlib.net.obsr;

public interface StoredObject {

    StoredObject getChild(String name);
    StoredEntry getEntry(String name);
}
