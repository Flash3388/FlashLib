package com.flash3388.flashlib.scheduling.statemachines;

import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public interface ActionAttacher {

    ActionAttacher onFinishTransition(Transition transition);
    ActionAttacher onFinishTransition(Transition transition, Set<FinishReason> reasons);

    default ActionAttacher onFinishTransition(Transition transition, FinishReason... reasons) {
        Objects.requireNonNull(reasons, "reasons is null");

        if (reasons.length == 0) {
            return onFinishTransition(transition);
        }

        return onFinishTransition(transition, new HashSet<>(Arrays.asList(reasons)));
    }

    StateConfigurer done();
}
