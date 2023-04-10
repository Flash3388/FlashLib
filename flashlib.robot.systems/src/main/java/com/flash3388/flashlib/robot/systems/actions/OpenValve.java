package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.Valve;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class OpenValve extends ActionBase {

    private final Valve mValve;

    public OpenValve(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute(ActionControl control) {
        mValve.open();

        control.finish();
    }
}
