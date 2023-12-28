package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.statemachines.State;
import com.flash3388.flashlib.scheduling.statemachines.StateConfigurer;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

import java.util.Collection;

public class StateConfigurerImpl implements StateConfigurer {

    private final StateContainer mContainer;

    public StateConfigurerImpl(StateContainer container) {
        mContainer = container;
    }

    @Override
    public StateConfigurer whileActive(Action action) {
        mContainer.addAction(action);
        return this;
    }

    @Override
    public StateConfigurer whileActive(Action action, Transition onFinish) {
        mContainer.addAction(action, onFinish);
        return this;
    }

    @Override
    public StateConfigurer onEnter(Runnable runnable) {
        mContainer.addOnEnter(runnable);
        return this;
    }

    @Override
    public StateConfigurer onExit(Runnable runnable) {
        mContainer.addOnExit(runnable);
        return this;
    }

    @Override
    public StateConfigurer allowTransitionTo(Collection<? extends State> states) {
        mContainer.allowTransitionTo(states);
        return this;
    }
}
