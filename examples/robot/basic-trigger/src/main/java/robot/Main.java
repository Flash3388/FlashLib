package robot;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;

public class Main {

    public static void main(String[] args) {
        RobotMain.start((instanceId, resourceHolder)-> {
            RobotControl robotControl = new GenericRobotControl(instanceId, resourceHolder,
                    GenericRobotControl.Configuration.controlStubs());
            RobotBase robotBase = new LoopingRobotBase(UserRobot::new);
            return new RobotImplementation(robotControl, robotBase);
        });
    }
}
