package robot;

import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.hid.sdl2.Sdl2HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.devices.DeviceInterfaceImpl;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.HidBackend;
import com.flash3388.flashlib.robot.base.IoBackend;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.robot.modes.ManualRobotModeSupplier;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.FlashLibMainThreadImpl;

public class Main {

    public static void main(String[] args) {
        RobotMain.start((instanceId, resourceHolder)-> {
            ManualRobotModeSupplier robotModeProperty = new ManualRobotModeSupplier();

            RobotControl robotControl = new GenericRobotControl(
                    instanceId, resourceHolder,
                    NetworkConfiguration.disabled(),
                    robotModeProperty,
                    GenericRobotControl.Configuration.custom(
                            false,
                            HidBackend.SDL2,
                            IoBackend.STUB
                    )
            );

            // When defining the creation of UserRobot, we make sure to pass the manual mode supplier to
            // the constructor, so it could be used.
            RobotBase robotBase = new LoopingRobotBase(rc -> new UserRobot(rc, robotModeProperty));
            return new RobotImplementation(robotControl, robotBase);
        });
    }
}
