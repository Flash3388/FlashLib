package com.flash3388.flashlib.util.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A storage for open resources ({@link AutoCloseable}) used throughout a program.
 * All resources can be safely closed at the end of the program using {@link #freeAll()}.
 * <p>
 *     <b>This is a single-use object, and should not be used after {@link #freeAll()} was called.</b>
 * </p>
 *
 * @since FlashLib 2.0.0
 */
public class ResourceHolder {

    private final Collection<AutoCloseable> mResources;

    private ResourceHolder(Collection<AutoCloseable> resources) {
        mResources = resources;
    }

    /**
     * Creates a new empty resource holder.
     *
     * @return resource empty holder.
     */
    public static ResourceHolder empty() {
        return new ResourceHolder(new ArrayList<>());
    }

    /**
     * Creates a new resource holder with several resources added.
     * <p>
     *     Equivalent to using <code>ResourceHolder.empty().add(resources)</code>.
     * </p>
     *
     * @param resources resources to add to holder.
     *
     * @return new resource holder
     */
    public static ResourceHolder with(AutoCloseable... resources) {
        return new ResourceHolder(Arrays.asList(resources));
    }

    /**
     * Adds a new resource to the holder, that will be freed when {@link #freeAll()}.
     *
     * @param closeable resource to add.
     *
     * @return this.
     */
    public ResourceHolder add(AutoCloseable closeable) {
        mResources.add(closeable);
        return this;
    }

    /**
     * Adds new resources to the holder, that will be freed when {@link #freeAll()}.
     *
     * @param closeables resources to add.
     *
     * @return this.
     */
    public ResourceHolder add(AutoCloseable... closeables) {
        return add(Arrays.asList(closeables));
    }

    /**
     * Adds new resources to the holder, that will be freed when {@link #freeAll()}.
     *
     * @param closeables resources to add.
     *
     * @return this.
     */
    public ResourceHolder add(Collection<? extends AutoCloseable> closeables) {
        mResources.addAll(closeables);
        return this;
    }

    /**
     * Frees all the stored resources by calling {@link AutoCloseable#close()} for each.
     * Guarantees to close all the resources, even if one has thrown an exception.
     *
     * @throws Throwable if one of the resources has thrown an exception while closing.
     *      Each additional exception thrown by a resource while be suppressed using
     *      {@link Throwable#addSuppressed(Throwable)}.
     */
    public void freeAll() throws Throwable {
        Throwable throwable = null;

        for (AutoCloseable closeable : mResources) {
            try {
                closeable.close();
            } catch (Throwable t) {
                if (throwable == null) {
                    throwable = t;
                } else {
                    throwable.addSuppressed(t);
                }
            }
        }

        if (throwable != null) {
            throw throwable;
        }
    }
}
