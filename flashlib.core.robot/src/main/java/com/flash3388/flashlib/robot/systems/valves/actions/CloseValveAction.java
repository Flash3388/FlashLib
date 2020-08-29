package com.flash3388.flashlib.robot.systems.valves.actions;

import com.flash3388.flashlib.robot.systems.valves.Valve;
import com.flash3388.flashlib.scheduling.actions.InstantAction;

public class CloseValveAction extends InstantAction {

    private final Valve mValve;

    public CloseValveAction(Valve valve) {
        mValve = valve;
    }

    @Override
    public void execute() {
        mValve.close();
    }
}
