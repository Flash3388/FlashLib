package robot.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import robot.subsystems.ElevatorSystem;

public class ElevatorUpToEdge extends ActionBase {

    private final ElevatorSystem mElevatorSystem;

    public ElevatorUpToEdge(ElevatorSystem elevatorSystem) {
        mElevatorSystem = elevatorSystem;

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
