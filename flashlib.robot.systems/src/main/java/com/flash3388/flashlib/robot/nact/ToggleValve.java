package com.flash3388.flashlib.robot.nact;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class ToggleValve extends ActionBase {

    private final Valve mValve;

    public ToggleValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute() {
        if (mValve.isOpen()) {
            mValve.close();
        } else {
            mValve.open();
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
