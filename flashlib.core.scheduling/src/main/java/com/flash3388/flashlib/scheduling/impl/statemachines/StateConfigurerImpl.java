package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.statemachines.ActionAttacher;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.StateConfigurer;

import java.util.Collection;
import java.util.function.Supplier;

public class StateConfigurerImpl implements StateConfigurer {

    private final StateContainer mContainer;

    public StateConfigurerImpl(StateContainer container) {
        mContainer = container;
    }

    @Override
    public StateConfigurer onEnter(Runnable runnable) {
        mContainer.setOnEnter(runnable);
        return this;
    }

    @Override
    public StateConfigurer onExit(Runnable runnable) {
        mContainer.setOnExit(runnable);
        return this;
    }

    @Override
    public StateConfigurer allowTransitionTo(Collection<? extends State> states) {
        mContainer.allowTransitionTo(states);
        return this;
    }

    @Override
    public ActionAttacher attach(Supplier<Action> actionSupplier) {
        return new ActionAttacherImpl(this, mContainer, actionSupplier);
    }
}
