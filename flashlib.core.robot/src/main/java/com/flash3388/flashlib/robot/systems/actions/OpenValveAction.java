package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.actions.InstantAction;

public class OpenValveAction extends InstantAction {

    private final Valve mValve;

    public OpenValveAction(Valve valve) {
        mValve = valve;

        requires(valve);
    }

    @Override
    public void execute() {
        mValve.open();
    }
}
