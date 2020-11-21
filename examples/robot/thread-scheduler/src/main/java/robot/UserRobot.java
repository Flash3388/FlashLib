package robot;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.base.DelegatingRobotControl;
import com.flash3388.flashlib.robot.base.iterative.IterativeRobot;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.Actions;
import com.flash3388.flashlib.time.Time;

public class UserRobot extends DelegatingRobotControl implements IterativeRobot {


    public UserRobot(RobotControl robotControl) throws RobotInitializationException {
        super(robotControl);

        Action action1 = Actions.periodic(()-> {
            System.out.println("task1");
        }, Time.milliseconds(500))
                .configure()
                .setName("task1")
                .setRunWhenDisabled(true)
                .save();
        action1.start();

        Action action2 = Actions.periodic(()-> {
            System.out.println("task2");
        }, Time.milliseconds(700))
                .configure()
                .setName("task2")
                .setRunWhenDisabled(true)
                .save();
        action2.start();

        Action action3 = Actions.wait(Time.seconds(2))
                .configure().setName("wait-2").setRunWhenDisabled(true).save()
                .andThen(Actions.instant(()-> {
                    System.out.println("cancel task1");
                    action1.cancel();
                }).configure().setName("cancel task1").setRunWhenDisabled(true).save());
        action3.start();
    }

    @Override
    public void robotPeriodic() {

    }

    @Override
    public void robotStop() {

    }

    @Override
    public void disabledInit() {

    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void modeInit(RobotMode mode) {

    }

    @Override
    public void modePeriodic(RobotMode mode) {

    }
}
