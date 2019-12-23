package com.flash3388.flashlib.util.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ResourceHolder {

    private final Collection<Resource> mResources;

    private ResourceHolder(Collection<Resource> resources) {
        mResources = resources;
    }

    public static ResourceHolder empty() {
        return new ResourceHolder(new ArrayList<>());
    }

    public static ResourceHolder with(Resource... resources) {
        return new ResourceHolder(Arrays.asList(resources));
    }

    public ResourceHolder add(Resource resource) {
        mResources.add(resource);
        return this;
    }

    public ResourceHolder add(Resource... resources) {
        return add(Arrays.asList(resources));
    }

    public ResourceHolder add(Collection<? extends Resource> resources) {
        mResources.addAll(resources);
        return this;
    }

    public void freeAll() {
        RuntimeException unexpectedException = null;

        for (Resource resource : mResources) {
            try {
                resource.free();
            } catch (RuntimeException e) {
                if (unexpectedException == null) {
                    unexpectedException = e;
                } else {
                    unexpectedException.addSuppressed(e);
                }
            }
        }

        if (unexpectedException != null) {
            throw unexpectedException;
        }
    }
}
