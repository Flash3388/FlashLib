package com.flash3388.flashlib.util.resources;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ResourceHolderTest {

    @Test
    public void freeAll_resourcesAdded_allResourcesFreed() throws Throwable {
        AutoCloseable[] RESOURCES = {
            mock(AutoCloseable.class),
            mock(AutoCloseable.class)
        };

        ResourceHolder resourceHolder = ResourceHolder.with(RESOURCES);
        resourceHolder.freeAll();

        for (AutoCloseable resource : RESOURCES) {
            verify(resource, times(1)).close();
        }
    }

    @Test
    public void freeAll_unexpectedExceptionThrown_allResourcesFreed() throws Throwable {
        AutoCloseable[] RESOURCES = {
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        };

        doThrow(new RuntimeException()).when(RESOURCES[0]).close();

        ResourceHolder resourceHolder = ResourceHolder.with(RESOURCES);
        try {
            resourceHolder.freeAll();
            fail("expected exception");
        } catch (Throwable t) {
            // expected
        }

        for (AutoCloseable resource : RESOURCES) {
            verify(resource, times(1)).close();
        }
    }
}