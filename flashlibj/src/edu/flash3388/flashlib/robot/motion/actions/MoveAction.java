package edu.flash3388.flashlib.robot.motion.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.motion.Movable;

import java.util.function.DoubleSupplier;

public class MoveAction extends Action {

    private final Movable mMovable;
    private final DoubleSupplier mSpeedSupplier;

    public MoveAction(Movable movable, DoubleSupplier speedSupplier) {
        mMovable = movable;
        mSpeedSupplier = speedSupplier;

        if(movable instanceof Subsystem) {
            requires((Subsystem) movable);
        }
    }

    @Override
    protected void execute() {
        mMovable.move(mSpeedSupplier.getAsDouble());
    }

    @Override
    protected void end() {
        mMovable.stop();
    }
}
