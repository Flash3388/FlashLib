package com.flash3388.flashlib.net.obsr;

/**
 * Represents an object stored in memory and shared across connected instance.
 *
 * An object is a named, tree-based data structure, containing entries with data (i.e. fields) and child
 * objects.
 *
 * @since FlashLib 3.2.0
 */
public interface StoredObject {

    /**
     * Gets a child object. If it doesn't exist, it is created and returned.
     *
     * @param name name of child object
     * @return child object
     */
    StoredObject getChild(String name);

    /**
     * Gets an entry for this object.
     *
     * @param name name of the entry
     * @return entry
     */
    StoredEntry getEntry(String name);

    void addListener(ObjectListener listener);
    void removeListener(ObjectListener listener);

    class Stub implements StoredObject {

        @Override
        public StoredObject getChild(String name) {
            return new Stub();
        }

        @Override
        public StoredEntry getEntry(String name) {
            return new StoredEntry.Stub();
        }

        @Override
        public void addListener(ObjectListener listener) {

        }

        @Override
        public void removeListener(ObjectListener listener) {

        }
    }
}
