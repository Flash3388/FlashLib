package edu.flash3388.flashlib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ResourceCloser {

    private final Collection<Resource> mResources;

    private ResourceCloser(Collection<Resource> resources) {
        mResources = resources;
    }

    public static ResourceCloser empty() {
        return new ResourceCloser(new ArrayList<Resource>());
    }

    public static ResourceCloser with(Resource... resources) {
        return new ResourceCloser(Arrays.asList(resources));
    }

    public ResourceCloser add(Resource resource) {
        mResources.add(resource);
        return this;
    }

    public void close() {
        for (Resource resource : mResources) {
            resource.free();
        }
    }
}
