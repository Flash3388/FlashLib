package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionConfigurationEditor;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.FinishReason;

import java.util.function.Consumer;

public class GenericAction implements ActionInterface {

    public static class Builder {

        private Consumer<ActionConfigurationEditor> mOnConfigure;
        private Consumer<ActionControl> mOnInitialize;
        private Consumer<ActionControl> mOnExecute;
        private Consumer<FinishReason> mOnEnd;

        public Builder onConfigure(Consumer<ActionConfigurationEditor> runnable) {
            mOnConfigure = runnable;
            return this;
        }

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

        public ActionInterface build() {
            return new GenericAction(mOnConfigure, mOnInitialize, mOnExecute, mOnEnd);
        }
    }

    private final Consumer<ActionConfigurationEditor> mOnConfigure;
    private final Consumer<ActionControl> mOnInitialize;
    private final Consumer<ActionControl> mOnExecute;
    private final Consumer<FinishReason> mOnEnd;

    public GenericAction(Consumer<ActionConfigurationEditor> onConfigure,
                         Consumer<ActionControl> onInitialize,
                         Consumer<ActionControl> onExecute,
                         Consumer<FinishReason> onEnd) {
        mOnConfigure = onConfigure;
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mOnEnd = onEnd;
    }

    @Override
    public void configure(ActionConfigurationEditor editor) {
        if (mOnConfigure != null) {
            mOnConfigure.accept(editor);
        }
    }

    @Override
    public void initialize(ActionControl control) {
        if (mOnInitialize != null) {
            mOnInitialize.accept(control);
        }
    }

    @Override
    public void execute(ActionControl control) {
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
