package robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.generic.DependencyProvider;
import com.flash3388.flashlib.robot.base.generic.GenericRobotControl;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
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
            RobotControl robotControl = new GenericRobotControl(l, resourceHolder,
                    DependencyProvider.cascadingInitializationBuilder(l, resourceHolder)
                            .add(()-> new StaticRobotModeSupplier(RobotMode.DISABLED))
                            .add(IoInterface.Stub::new)
                            .add(HidInterface.Stub::new)
                            .add(RobotFactory::newDefaultClock)
                            .add(RobotFactory::disabledNetworkInterface)
                            .add((dependencies)-> {
                                Clock clock = dependencies.get(Clock.class);
                                return RobotFactory.newDefaultScheduler(clock, l);
                            })
                            .build());
            RobotBase robotBase = new LoopingRobotBase(UserRobot::new);

            return new RobotImplementation(robotControl, robotBase);
        }, logger);
    }
}
