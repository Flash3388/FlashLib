package robot;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotImplementation;
import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.robot.base.GenericRobotControl;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .build();

        RobotMain.start((instanceId, resourceHolder, aLogger)-> {
            RobotControl robotControl = new GenericRobotControl(instanceId, resourceHolder, aLogger);
            RobotBase robotBase = new LoopingRobotBase(UserRobot::new);
            return new RobotImplementation(robotControl, robotBase);
        }, logger);
    }
}
