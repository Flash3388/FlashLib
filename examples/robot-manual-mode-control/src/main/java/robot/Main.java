package robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.RobotImpl;
import com.flash3388.flashlib.robot.base.generic.DependencyProvider;
import com.flash3388.flashlib.robot.base.generic.GenericRobotControl;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.robot.modes.ManualRobotModeSupplier;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .build();

        RobotMain.start((l, resourceHolder)-> {
            // We'll use ManualRobotModeSupplier, it will allow us to set the value from the robot
            // only using set(RobotMode).
            ManualRobotModeSupplier manualRobotModeSupplier = new ManualRobotModeSupplier();
            manualRobotModeSupplier.set(RobotMode.DISABLED);

            RobotControl robotControl = new GenericRobotControl(l, resourceHolder,
                    DependencyProvider.cascadingInitializationBuilder(l, resourceHolder)
                            // Still need to pass the mode supplier here to the robot control.
                            .add(()-> manualRobotModeSupplier)
                            .add(IoInterface.Stub::new)
                            .add(HidInterface.Stub::new)
                            .add(RobotFactory::newDefaultClock)
                            .add((dependencies)-> {
                                Clock clock = dependencies.get(Clock.class);
                                return RobotFactory.newDefaultScheduler(clock, l);
                            })
                            .build());

            // When defining the creation of UserRobot, we make sure to pass the manual mode supplier to
            // the constructor, so it could be used.
            RobotBase robotBase = new LoopingRobotBase(rc -> new UserRobot(rc, manualRobotModeSupplier));

            return new RobotImpl(robotControl, robotBase);
        }, logger);
    }
}
