package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class OpenValveAction extends ActionBase {

    private final Valve mValve;

    public OpenValveAction(Valve valve) {
        mValve = valve;

        requires(valve);
    }

    @Override
    public void execute(ActionControl control) {
        mValve.open();
        control.finish();
    }
}
