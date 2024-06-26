package robot;

import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.concurrent.DefaultFlashLibThreadFactory;
import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.devices.DeviceInterfaceImpl;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.robot.modes.ManualRobotModeSupplier;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.FlashLibMainThreadImpl;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;

public class Main {

    public static void main(String[] args) {
        RobotMain.start((instanceId, resourceHolder)-> {
            FlashLibMainThread mainThread = new FlashLibMainThreadImpl();

            // We'll use ManualRobotModeSupplier, it will allow us to set the value from the robot
            // only using set(RobotMode).
            ManualRobotModeSupplier manualRobotModeSupplier = new ManualRobotModeSupplier();
            manualRobotModeSupplier.set(RobotMode.DISABLED);

            Clock clock = RobotFactory.newDefaultClock();
            ServiceRegistry serviceRegistry = new BasicServiceRegistry(mainThread);

            NamedThreadFactory threadFactory = new DefaultFlashLibThreadFactory();
            RobotControl robotControl = new GenericRobotControl(
                    instanceId,
                    resourceHolder,
                    threadFactory,
                    manualRobotModeSupplier,
                    new NetworkInterfaceImpl(NetworkConfiguration.disabled(),
                            instanceId, serviceRegistry, clock, mainThread, threadFactory),
                    new IoInterface.Stub(),
                    new HidInterface.Stub(),
                    RobotFactory.newDefaultScheduler(clock),
                    clock,
                    serviceRegistry,
                    mainThread,
                    new DeviceInterfaceImpl(mainThread));

            // When defining the creation of UserRobot, we make sure to pass the manual mode supplier to
            // the constructor, so it could be used.
            RobotBase robotBase = new LoopingRobotBase(rc -> new UserRobot(rc, manualRobotModeSupplier));
            return new RobotImplementation(robotControl, robotBase);
        });
    }
}
