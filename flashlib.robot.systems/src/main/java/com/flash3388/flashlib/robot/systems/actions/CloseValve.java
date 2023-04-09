package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.Valve;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class CloseValve extends ActionBase {

    private final Valve mValve;

    public CloseValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute(ActionControl control) {
        mValve.close();

        control.finish();
    }
}
