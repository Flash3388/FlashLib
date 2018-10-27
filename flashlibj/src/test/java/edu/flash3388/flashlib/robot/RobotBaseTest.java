package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.Resource;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class RobotBaseTest {

    @Test
    public void resourceFreeing_finishedRunSuccessfully_allResourcesFreed() throws Exception {
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
}