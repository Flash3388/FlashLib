package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GenericAction extends ActionBase {

    public static class Builder {
        private Runnable mOnInitialize;
        private Consumer<ActionControl> mOnExecute;
        private Consumer<FinishReason> mOnEnd;

        public Builder onInitialize(Runnable runnable) {
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

    private final Runnable mOnInitialize;
    private final Consumer<ActionControl> mOnExecute;
    private final Consumer<FinishReason> mOnEnd;

    public GenericAction(Runnable onInitialize, Consumer<ActionControl> onExecute, Consumer<FinishReason> onEnd) {
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mOnEnd = onEnd;
    }

    @Override
    public final void initialize() {
        if (mOnInitialize != null) {
            mOnInitialize.run();
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
