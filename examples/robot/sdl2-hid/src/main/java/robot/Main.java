package robot;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.hid.sdl2.Sdl2HidInterface;
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
import com.flash3388.flashlib.util.logging.LogLevel;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .setLogLevel(LogLevel.DEBUG)
                .enableConsoleLogging(true)
                .build();

        RobotMain.start((l, resourceHolder)-> {
            Property<RobotMode> robotModeProperty = new AtomicProperty<>(RobotMode.DISABLED);

            RobotControl robotControl = new GenericRobotControl(l, resourceHolder,
                    DependencyProvider.cascadingInitializationBuilder(l, resourceHolder)
                    .add(()-> robotModeProperty)
                    .add(IoInterface.Stub::new)
                    .add(()-> new WeakHidInterface(new Sdl2HidInterface()))
                    .add(RobotFactory::newDefaultClock)
                    .add(RobotFactory::disabledNetworkInterface)
                    .add((dependencies)-> {
                        Clock clock = dependencies.get(Clock.class);
                        return RobotFactory.newDefaultScheduler(clock, l);
                    })
                    .build());
            RobotBase robotBase = new LoopingRobotBase((rc)-> new UserRobot(rc, robotModeProperty));

            return new RobotImplementation(robotControl, robotBase);
        }, logger);
    }
}
