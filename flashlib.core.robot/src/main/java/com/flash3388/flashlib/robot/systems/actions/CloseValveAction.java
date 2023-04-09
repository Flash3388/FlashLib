package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class CloseValveAction extends ActionBase {

    private final Valve mValve;

    public CloseValveAction(Valve valve) {
        mValve = valve;

        requires(valve);
    }

    @Override
    public void execute(ActionControl control) {
        mValve.close();
        control.finish();
    }
}
