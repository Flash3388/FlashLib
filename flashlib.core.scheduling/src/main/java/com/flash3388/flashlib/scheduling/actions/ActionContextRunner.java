package com.flash3388.flashlib.scheduling.actions;

import java.util.function.Predicate;

class ActionContextRunner implements Predicate<ActionContext> {

    @Override
    public boolean test(ActionContext actionContext) {
        if (!actionContext.run()) {
            actionContext.runFinished();

            return true;
        }

        return false;
    }
}
