package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.Valve;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class ToggleValve extends ActionBase {

    private final Valve mValve;

    public ToggleValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute(ActionControl control) {
        if (mValve.isOpen()) {
            mValve.close();
        } else {
            mValve.open();
        }

        control.finish();
    }
}
