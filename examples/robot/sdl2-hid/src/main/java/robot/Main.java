package robot;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.hid.sdl2.Sdl2HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Clock;

public class Main {

    public static void main(String[] args) {
        RobotMain.start((instanceId, resourceHolder)-> {
            Property<RobotMode> robotModeProperty = new AtomicProperty<>(RobotMode.DISABLED);

            Clock clock = RobotFactory.newDefaultClock();

            RobotControl robotControl = new GenericRobotControl(
                    instanceId, resourceHolder,
                    robotModeProperty,
                    RobotFactory.disabledNetworkInterface(),
                    new IoInterface.Stub(),
                    new WeakHidInterface(new Sdl2HidInterface()),
                    RobotFactory.newDefaultScheduler(clock),
                    clock,
                    new BasicServiceRegistry());

            // When defining the creation of UserRobot, we make sure to pass the manual mode supplier to
            // the constructor, so it could be used.
            RobotBase robotBase = new LoopingRobotBase(rc -> new UserRobot(rc, robotModeProperty));
            return new RobotImplementation(robotControl, robotBase);
        });
    }
}
