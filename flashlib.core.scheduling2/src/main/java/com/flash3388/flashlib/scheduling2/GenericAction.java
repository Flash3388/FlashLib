package com.flash3388.flashlib.scheduling2;

import java.util.function.Consumer;

public class GenericAction implements Action {

    public static class Builder {

        private Consumer<Configuration> mOnConfigure;
        private Consumer<Control> mOnInitialize;
        private Consumer<Control> mOnExecute;
        private Consumer<Control> mOnEnd;

        public Builder() {
            mOnConfigure = null;
            mOnInitialize = null;
            mOnExecute = null;
            mOnEnd = null;
        }

        public Builder configure(Consumer<Configuration> consumer) {
            mOnConfigure = consumer;
            return this;
        }

        public Builder initialize(Consumer<Control> consumer) {
            mOnInitialize = consumer;
            return this;
        }

        public Builder execute(Consumer<Control> consumer) {
            mOnExecute = consumer;
            return this;
        }

        public Builder end(Consumer<Control> consumer) {
            mOnEnd = consumer;
            return this;
        }

        public GenericAction build() {
            return new GenericAction(mOnConfigure, mOnInitialize, mOnExecute, mOnEnd);
        }
    }

    private final Consumer<Configuration> mOnConfigure;
    private final Consumer<Control> mOnInitialize;
    private final Consumer<Control> mOnExecute;
    private final Consumer<Control> mOnEnd;

    public GenericAction(Consumer<Configuration> onConfigure, Consumer<Control> onInitialize,
                         Consumer<Control> onExecute, Consumer<Control> onEnd) {
        mOnConfigure = onConfigure;
        mOnInitialize = onInitialize;
        mOnExecute = onExecute;
        mOnEnd = onEnd;
    }

    public GenericAction() {
        this(null, null, null, null);
    }

    @Override
    public void configure(Configuration configuration) {
        if (mOnConfigure != null) {
            mOnConfigure.accept(configuration);
        }
    }

    @Override
    public void initialize(Control control) {
        if (mOnInitialize != null) {
            mOnInitialize.accept(control);
        }
    }

    @Override
    public void execute(Control control) {
        if (mOnExecute != null) {
            mOnExecute.accept(control);
        }
    }

    @Override
    public void end(Control control) {
        if (mOnEnd != null) {
            mOnEnd.accept(control);
        }
    }
}
