package robot;

import com.flash3388.flashlib.robot.base.RobotFactory;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.generic.DependencyProvider;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotControl;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .build();

        RobotMain.start((l, resourceHolder)-> new LoopingRobotControl(l, resourceHolder,
                DependencyProvider.cascadingInitializationBuilder(l, resourceHolder)
                    .add(()-> new StaticRobotModeSupplier(RobotMode.DISABLED))
                    .add(IoInterface.Stub::new)
                    .add(HidInterface.Stub::new)
                    .add(RobotFactory::newDefaultClock)
                    .add((dependencies)-> {
                        Clock clock = dependencies.get(Clock.class);
                        return RobotFactory.newDefaultScheduler(clock, l);
                    })
                    .build(),
                UserRobot::new), logger);
    }
}
