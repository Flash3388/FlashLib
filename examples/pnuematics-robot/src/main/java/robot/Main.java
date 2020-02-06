package robot;

import com.flash3388.flashlib.robot.RobotMain;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

public class Main {

    public static void main(String[] args) {
        Logger logger = new LoggerBuilder("robot")
                .build();

        RobotMain.start(Robot::new, logger);
    }
}
