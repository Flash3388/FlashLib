package edu.flash3388.flashlib.util;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class ResourceCloserTest {

    @Test
    public void freeAll_resourcesAdded_allResourcesFreed() throws Exception {
        Resource[] RESOURCES = {
            mock(Resource.class),
            mock(Resource.class)
        };

        ResourceCloser resourceCloser = ResourceCloser.with(RESOURCES);
        resourceCloser.close();

        for (Resource resource : RESOURCES) {
            verify(resource, times(1)).free();
        }
    }
}