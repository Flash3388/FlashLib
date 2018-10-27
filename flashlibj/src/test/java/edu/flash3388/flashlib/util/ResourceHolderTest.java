package edu.flash3388.flashlib.util;

import org.junit.Test;

import static org.mockito.Mockito.*;

public class ResourceHolderTest {

    @Test
    public void freeAll_resourcesAdded_allResourcesFreed() throws Exception {
        Resource[] RESOURCES = {
            mock(Resource.class),
            mock(Resource.class)
        };

        ResourceHolder resourceHolder = ResourceHolder.with(RESOURCES);
        resourceHolder.freeAll();

        for (Resource resource : RESOURCES) {
            verify(resource, times(1)).free();
        }
    }
}