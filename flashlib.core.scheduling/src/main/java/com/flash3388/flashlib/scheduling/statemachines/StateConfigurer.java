package com.flash3388.flashlib.scheduling.statemachines;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public interface StateConfigurer {

    StateConfigurer onEnter(Runnable runnable);
    StateConfigurer onExit(Runnable runnable);

    StateConfigurer allowTransitionTo(Collection<? extends State> states);

    default StateConfigurer allowTransitionTo(State... states) {
        return allowTransitionTo(Arrays.asList(states));
    }

    ActionAttacher attach(Supplier<Action> actionSupplier);

    default ActionAttacher attach(Action action) {
        return attach(Suppliers.of(action));
    }
}
