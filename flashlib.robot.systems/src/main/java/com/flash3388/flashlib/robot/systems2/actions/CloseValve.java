package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class CloseValve extends ActionBase {

    private final Valve mValve;

    public CloseValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute() {
        mValve.close();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
