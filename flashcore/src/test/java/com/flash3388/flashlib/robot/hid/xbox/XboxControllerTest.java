package com.flash3388.flashlib.robot.hid.xbox;

import com.flash3388.flashlib.robot.RunningRobotMock;
import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.hid.Pov;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.StaticClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;


public class XboxControllerTest {

    @BeforeEach
    public void setup() throws Exception {
        RunningRobotMock.mockRobotWithDependencies();
    }

    @ParameterizedTest(name = "getButton(type {0}) == getButton(index {0}.index)")
    @EnumSource(value = XboxButton.class)
    public void getButtonByType_getButtonByMatchingIndex_returnsSameButton(XboxButton button) throws Exception {
        XboxController xboxController = new XboxController(mock(Scheduler.class), new StaticClock(), new HidInterface.Stub(), 0);
        assertEquals(xboxController.getButton(button), xboxController.getButton(button.buttonIndex()));
    }

    @ParameterizedTest(name = "getAxis(type {0}) == getAxis(index {0}.index)")
    @EnumSource(value = XboxAxis.class)
    public void getAxisByType_getAxisByMatchingIndex_returnsSameButton(XboxAxis axis) throws Exception {
        XboxController xboxController = new XboxController(mock(Scheduler.class), new StaticClock(), new HidInterface.Stub(), 0);
        assertEquals(xboxController.getAxis(axis), xboxController.getAxis(axis.axisIndex()));
    }

    @Test
    public void axes_normal_returnsIterableOfTheAxisCount() throws Exception {
        XboxController xboxController = new XboxController(mock(Scheduler.class), new StaticClock(), new HidInterface.Stub(), 0);
        Iterable<Axis> axes = xboxController.axes();

        assertEquals(xboxController.getAxisCount(), getIterableSize(axes));
    }

    @Test
    public void buttons_normal_returnsIterableOfTheButtonCount() throws Exception {
        XboxController xboxController = new XboxController(mock(Scheduler.class), new StaticClock(), new HidInterface.Stub(), 0);
        Iterable<Button> buttons = xboxController.buttons();

        assertEquals(xboxController.getButtonCount(), getIterableSize(buttons));
    }

    @Test
    public void povs_normal_returnsIterableOfThePovCount() throws Exception {
        XboxController xboxController = new XboxController(mock(Scheduler.class), new StaticClock(), new HidInterface.Stub(), 0);
        Iterable<Pov> povs = xboxController.povs();

        assertEquals(xboxController.getPovCount(), getIterableSize(povs));
    }

    private <T> int getIterableSize(Iterable<T> iterable) {
        AtomicInteger count = new AtomicInteger();
        iterable.forEach((i) -> count.getAndIncrement());

        return count.get();
    }
}