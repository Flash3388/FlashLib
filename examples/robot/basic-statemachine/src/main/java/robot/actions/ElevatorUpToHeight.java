package robot.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import robot.subsystems.ElevatorSystem;

public class ElevatorUpToHeight extends ActionBase {

    private final ElevatorSystem mElevatorSystem;
    private final double mTargetHeight;

    public ElevatorUpToHeight(ElevatorSystem elevatorSystem, double targetHeight) {
        mElevatorSystem = elevatorSystem;
        mTargetHeight = targetHeight;

        requires(mElevatorSystem);
    }

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {

    }

    @Override
    public void end(FinishReason reason) {

    }
}
