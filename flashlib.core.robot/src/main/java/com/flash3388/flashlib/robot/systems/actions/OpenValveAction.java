package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;

public class OpenValveAction implements ActionInterface {

    private final Valve mValve;

    public OpenValveAction(Valve valve) {
        mValve = valve;
    }

    @Override
    public void configure(ActionConfigurationEditor editor) {
        editor.addRequirements(mValve);
    }

    @Override
    public void execute(ActionControl control) {
        mValve.open();
    }
}
