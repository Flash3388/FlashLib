package com.flash3388.flashlib.scheduling2.imp;

import com.flash3388.flashlib.scheduling2.Action;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Control;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SequentialGroup implements Action {

    private static class Executing {
        Configuration configuration;

        Executing(Action action, Configuration configuration) {
            this.configuration = configuration;
        }
    }

    private final List<Action> mActions;
    private final List<Executing> mExecuting;
    private int mCurrentActionIndex;


    public SequentialGroup(List<Action> actions) {
        mActions = actions;
        mExecuting = new LinkedList<>();
    }

    @Override
    public void configure(Configuration configuration) {
        for (Action action : mActions) {
            Configuration actionConfig = new ConfigurationImpl();
            action.configure(actionConfig);

            configuration.requires(actionConfig.getRequirements());
            // TODO: HANDLE FLAGS
            
            mExecuting.add(new Executing(action, actionConfig));
        }
    }

    @Override
    public void initialize(Control control) {
        mCurrentActionIndex = 0;
    }

    @Override
    public void execute(Control control) {

    }

    @Override
    public void end(Control control) {

    }
}
