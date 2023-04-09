package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GenericAction extends ActionBase {

    public static class Builder {
        private Consumer<ActionControl> mOnInitialize;
        private Consumer<ActionControl> mOnExecute;
        private Consumer<FinishReason> mOnEnd;

        public Builder onInitialize(Consumer<ActionControl> runnable) {
            mOnInitialize = runnable;
            return this;
        }

        public Builder onExecute(Consumer<ActionControl> runnable) {
            mOnExecute = runnable;
            return this;
        }

        public Builder onEnd(Consumer<FinishReason> consumer) {
            mOnEnd = consumer;
            return this;
        }

        public Action build() {
            return new GenericAction(mOnInitialize, mOnExecute, mOnEnd);
        }
    }

    private final Consumer<ActionControl> mOnInitialize;
    private final Consumer<ActionControl> mOnExecute;
    private final Consumer<FinishReason> mOnEnd;

    public GenericAction(Consumer<ActionControl> onInitialize, Consumer<ActionControl> onExecute, Consumer<FinishReason> onEnd) {
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mOnEnd = onEnd;
    }

    @Override
    public final void initialize(ActionControl control) {
        if (mOnInitialize != null) {
            mOnInitialize.accept(control);
        }
    }

    @Override
    public final void execute(ActionControl control) {
        if (mOnExecute != null) {
            mOnExecute.accept(control);
        }
    }

    @Override
    public void end(FinishReason reason) {
        if (mOnEnd != null) {
            mOnEnd.accept(reason);
        }
    }
}
