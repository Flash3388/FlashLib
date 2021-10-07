package com.flash3388.flashlib.scheduling2.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ParallelActionGroup extends ActionGroupBase {

    private final Collection<ActionContext> mRunningActions;

    public ParallelActionGroup() {
        super(new ArrayList<>(), false);
        mRunningActions = new ArrayList<>();
    }

    @Override
    public void configure(Configuration configuration) {
        for (Action action : mActions) {
            Configuration cfg = new ConfigurationImpl();
            action.configure(cfg);

            if (!mAllowRequirementCollisions) {
                if (!Collections.disjoint(cfg.getRequirements(),
                        configuration.getRequirements())) {
                    throw new IllegalArgumentException("Actions cannot share requirements");
                }
            }

            configuration.requires(cfg.getRequirements());

            ActionContext actionContext = new ActionContext(
                    action, cfg, new StatusImpl(),
            );

            mRunningActions.add(actionContext);
        }
    }

    @Override
    public void initialize(Control control) {
        for (ActionContext context : mRunningActions) {
            
        }
    }

    @Override
    public void execute(Control control) {

    }

    @Override
    public void end(Control control) {

    }
}
