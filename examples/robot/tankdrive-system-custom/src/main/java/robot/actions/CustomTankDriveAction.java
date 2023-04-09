package robot.actions;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.JoystickAxis;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import robot.subsystems.CustomTankDrive;

public class CustomTankDriveAction extends ActionBase {
    // This action, is a simple recreation of the TankDriveAction.
    // To implement an action, we first extend ActionBase.

    private final CustomTankDrive mDrive;
    private final Joystick mStickRight;
    private final Joystick mStickLeft;

    // In the constructor, we receive the systems and parameters we need to perform the action.
    // Here we need the drive system, and the joysticks.
    // We will store those values in instance variables.
    public CustomTankDriveAction(CustomTankDrive drive, Joystick stickRight, Joystick stickLeft) {
        mDrive = drive;
        mStickRight = stickRight;
        mStickLeft = stickLeft;

        // Now we need to declare drive as a requirement for this action,
        // reporting to the Scheduler that it is used here in order
        // to prevent 2 actions from using this system at the same time.
        requires(drive);
    }

    // Now we can start implementing the action's lifecycle.
    // This lifecycle will define what the action should do when:
    //
    // initialize: runs once, each time the action is started. We would normally use it to initialize
    // states/variables/dependencies, in preparation for the execute phase. In our case, we have no need.
    //
    // execute: the main phase for an action. Runs periodically (timing differs but will likely be around 25ms).
    // In it we will perform the main logic of the action. In this case, moving the drive system.
    //
    // isFinished: also a part of the execute phase. It defines when the action should stop. If it returns true,
    // the action will stop running. In our case, we don't really want to stop the action, from our end, so we will return
    // false. If someone wants to stop this action, they can cancel it, or overwrite it with another one.
    //
    // end: the end phase runs after the execution phase. In this phase we stop and deinitialize anything used during the
    // action. In our case, that would mean stopping the drive system.
    // Note the wasInterrupted parameter. If the action finished normally, i.e. isFinished returned true, wasInterrupted
    // would be false. If someone canceled the action, it reached a timeout, or was overwritten by another action;
    // wasInterrupted would be true.

    @Override
    public void initialize(ActionControl control) {
    }

    @Override
    public void execute(ActionControl control) {
        // We grab the values from the joysticks.
        // - right: right stick axis Y
        // - left: left stick axis Y
        double right = mStickRight.getAxis(JoystickAxis.Y).getAsDouble();
        double left = mStickLeft.getAxis(JoystickAxis.Y).getAsDouble();
        // Move the drive system with the values from the joysticks.
        mDrive.tankDrive(right, left);
    }

    @Override
    public void end(FinishReason reason) {
        // When the action is done, we should stop the drive system.
        mDrive.stop();
    }
}
