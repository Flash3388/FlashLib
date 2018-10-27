package edu.flash3388.flashlib.util.resources;

public class AutoCloseableResource implements AutoCloseable {

    private final Resource mResource;

    public AutoCloseableResource(Resource resource) {
        mResource = resource;
    }

    @Override
    public void close() {
        mResource.free();
    }
}
