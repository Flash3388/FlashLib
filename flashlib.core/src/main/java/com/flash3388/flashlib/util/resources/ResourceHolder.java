package com.flash3388.flashlib.util.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ResourceHolder {

    private final Collection<AutoCloseable> mResources;

    private ResourceHolder(Collection<AutoCloseable> resources) {
        mResources = resources;
    }

    public static ResourceHolder empty() {
        return new ResourceHolder(new ArrayList<>());
    }

    public static ResourceHolder with(AutoCloseable... resources) {
        return new ResourceHolder(Arrays.asList(resources));
    }

    public ResourceHolder add(AutoCloseable closeable) {
        mResources.add(closeable);
        return this;
    }

    public ResourceHolder add(AutoCloseable... closeables) {
        return add(Arrays.asList(closeables));
    }

    public ResourceHolder add(Collection<? extends AutoCloseable> closeables) {
        mResources.addAll(closeables);
        return this;
    }

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
