package com.flash3388.flashlib.robot.hid.xbox;

import com.flash3388.flashlib.robot.hid.Axis;
import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.EmptyHidInterface;
import com.flash3388.flashlib.robot.hid.Pov;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class XboxControllerTest {

    @RunWith(Parameterized.class)
    public static class ButtonTypeTest {

        @Parameterized.Parameter(0)
        public XboxButton mButton;

        @Parameterized.Parameters(name = "Button {0}")
        public static Collection<Object[]> data() {
            return Arrays.stream(XboxButton.values()).map((button) -> new Object[] {button}).collect(Collectors.toList());
        }

        @Test
        public void getButtonByType_getButtonByMatchingIndex_returnsSameButton() throws Exception {
            XboxController xboxController = new XboxController(new EmptyHidInterface(), 0);
            assertEquals(xboxController.getButton(mButton), xboxController.getButton(mButton.buttonIndex()));
        }
    }

    @RunWith(Parameterized.class)
    public static class AxisTypeTest {

        @Parameterized.Parameter(0)
        public XboxAxis mAxis;

        @Parameterized.Parameters(name = "Axis {0}")
        public static Collection<Object[]> data() {
            return Arrays.stream(XboxAxis.values()).map((button) -> new Object[] {button}).collect(Collectors.toList());
        }

        @Test
        public void getAxisByType_getAxisByMatchingIndex_returnsSameButton() throws Exception {
            XboxController xboxController = new XboxController(new EmptyHidInterface(), 0);
            assertEquals(xboxController.getAxis(mAxis), xboxController.getAxis(mAxis.axisIndex()));
        }
    }

    public static class ComponentsAccessTest {

        @Test
        public void axes_normal_returnsIterableOfTheAxisCount() throws Exception {
            XboxController xboxController = new XboxController(new EmptyHidInterface(), 0);
            Iterable<Axis> axes = xboxController.axes();

            assertEquals(xboxController.getAxisCount(), getIterableSize(axes));
        }

        @Test
        public void buttons_normal_returnsIterableOfTheButtonCount() throws Exception {
            XboxController xboxController = new XboxController(new EmptyHidInterface(), 0);
            Iterable<Button> buttons = xboxController.buttons();

            assertEquals(xboxController.getButtonCount(), getIterableSize(buttons));
        }

        @Test
        public void povs_normal_returnsIterableOfThePovCount() throws Exception {
            XboxController xboxController = new XboxController(new EmptyHidInterface(), 0);
            Iterable<Pov> povs = xboxController.povs();

            assertEquals(xboxController.getPovCount(), getIterableSize(povs));
        }

        private <T> int getIterableSize(Iterable<T> iterable) {
            AtomicInteger count = new AtomicInteger();
            iterable.forEach((i) -> count.getAndIncrement());

            return count.get();
        }
    }
}