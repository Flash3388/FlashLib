package com.flash3388.flashlib.robot.systems.pneumatics.actions;

import com.flash3388.flashlib.robot.scheduling.actions.InstantAction;
import com.flash3388.flashlib.robot.systems.pneumatics.Piston;

public class OpenPistonAction extends InstantAction {

    private final Piston mPiston;

    public OpenPistonAction(Piston piston) {
        mPiston = piston;
    }

    @Override
    public void execute() {
        mPiston.open();
    }
}
