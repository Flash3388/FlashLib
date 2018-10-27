package edu.flash3388.flashlib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ResourceHolder {

    private final Collection<Resource> mResources;

    private ResourceHolder(Collection<Resource> resources) {
        mResources = resources;
    }

    public static ResourceHolder empty() {
        return new ResourceHolder(new ArrayList<Resource>());
    }

    public static ResourceHolder with(Resource... resources) {
        return new ResourceHolder(Arrays.asList(resources));
    }

    public ResourceHolder add(Resource resource) {
        mResources.add(resource);
        return this;
    }

    public void freeAll() {
        for (Resource resource : mResources) {
            resource.free();
        }
    }
}
