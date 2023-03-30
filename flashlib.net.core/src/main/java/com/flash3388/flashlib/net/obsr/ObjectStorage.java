package com.flash3388.flashlib.net.obsr;

/**
 * ObjectStorage is a data storage for {@link StoredObject}, which are named data structures, stored in
 * a hierarchy, and sharable across multiple machines and processes.
 *
 * It may be used to store information and share across different programs, allowing both to access and
 * modify it seamlessly.
 *
 * @since FlashLib 3.2.0
 */
public interface ObjectStorage {

    /**
     * Get the root object in the storage.
     *
     * @return root object
     */
    StoredObject getRoot();

    /**
     * Get the instance-specific root object.
     * This object is a child of {@link #getRoot()} and is identified by the
     * instance id of the current instance.
     *
     * @return root object
     */
    StoredObject getInstanceRoot();

    class Stub implements ObjectStorage {

        @Override
        public StoredObject getRoot() {
            return new StoredObject.Stub();
        }

        @Override
        public StoredObject getInstanceRoot() {
            return new StoredObject.Stub();
        }
    }
}
