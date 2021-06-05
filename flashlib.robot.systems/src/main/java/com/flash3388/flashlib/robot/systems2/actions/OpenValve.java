package com.flash3388.flashlib.robot.systems2.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class OpenValve extends ActionBase {

    private final Valve mValve;

    public OpenValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute() {
        mValve.open();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
