package com.flash3388.flashlib.robot.systems.pneumatics.actions;

import com.flash3388.flashlib.robot.systems.pneumatics.Piston;
import com.flash3388.flashlib.scheduling.actions.InstantAction;

public class ClosePistonAction extends InstantAction {

    private final Piston mPiston;

    public ClosePistonAction(Piston piston) {
        mPiston = piston;
    }

    @Override
    public void execute() {
        mPiston.close();
    }
}