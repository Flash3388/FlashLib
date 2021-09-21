package robot;

import com.castle.util.dependencies.DependencyContainer;
import com.castle.util.dependencies.DependencySupplier;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.DependencyProvider;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.robot.modes.ManualRobotModeSupplier;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.LoggerBuilder;

public class Main {

    public static void main(String[] args) {
        RobotMain.start((managers, logger)-> {
            // We'll use ManualRobotModeSupplier, it will allow us to set the value from the robot
            // only using set(RobotMode).
            ManualRobotModeSupplier manualRobotModeSupplier = new ManualRobotModeSupplier();
            manualRobotModeSupplier.set(RobotMode.DISABLED);

            DependencyContainer container = DependencyContainer.builder()
                    // Still need to pass the mode supplier here to the robot control.
                    .add(new DependencySupplier.Static(manualRobotModeSupplier))
                    .add(new DependencySupplier.Static(new IoInterface.Stub()))
                    .add(new DependencySupplier.Static(new HidInterface.Stub()))
                    .add(new DependencySupplier.Static(RobotFactory.newDefaultClock()))
                    .add((dependencies)-> {
                        Clock clock = dependencies.get(Clock.class);
                        return new DependencySupplier.Static(RobotFactory.newDefaultScheduler(clock, logger));
                    })
                    .build();

            RobotControl robotControl = new GenericRobotControl(new DependencyProvider(managers, container, logger));
            // When defining the creation of UserRobot, we make sure to pass the manual mode supplier to
            // the constructor, so it could be used.
            RobotBase robotBase = new LoopingRobotBase(rc -> new UserRobot(rc, manualRobotModeSupplier));

            return new RobotImplementation(robotControl, robotBase);
        }, new LoggerBuilder("robot")
                .build());
    }
}
