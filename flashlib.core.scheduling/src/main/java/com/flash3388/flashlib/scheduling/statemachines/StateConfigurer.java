package com.flash3388.flashlib.scheduling.statemachines;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Arrays;
import java.util.Collection;

public interface StateConfigurer {

    StateConfigurer whileActive(Action action);
    StateConfigurer whileActive(Action action, Transition onFinish);

    StateConfigurer onEnter(Runnable runnable);
    StateConfigurer onExit(Runnable runnable);

    StateConfigurer allowTransitionTo(Collection<? extends State> states);

    default StateConfigurer allowTransitionTo(State... states) {
        return allowTransitionTo(Arrays.asList(states));
    }
}
