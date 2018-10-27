package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.resources.Resource;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class RobotBaseTest {

    @Test
    public void stop_resourcesAvailable_allResourcesFreed() throws Exception {
        Resource[] RESOURCES = {
            mock(Resource.class),
            mock(Resource.class)
        };

        RobotBase robotBase = new FakeRobotBase();
        robotBase.registerResources(RESOURCES);

        robotBase.stop();

        for (Resource resource : RESOURCES) {
            verify(resource, times(1)).free();
        }
    }

    @Test
    public void initialize_initializationError_allResourcesFreed() throws Exception {
        Resource[] RESOURCES = {
                mock(Resource.class),
                mock(Resource.class)
        };

        RobotBase robotBase = spy(new FakeRobotBase());
        doThrow(new RobotInitializationException()).when(robotBase).robotInit();

        robotBase.registerResources(RESOURCES);

        try {
            robotBase.initialize();
            fail("expected exception");
        } catch (RobotInitializationException e) {
            // expected
        }

        for (Resource resource : RESOURCES) {
            verify(resource, times(1)).free();
        }
    }
}