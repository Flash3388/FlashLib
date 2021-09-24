package com.flash3388.flashlib.scheduling2.actions;

public interface Step<R> {

    boolean canRunInDisabled();

    Step<R> execute(ActionContext<R> context);
    Step<R> onError(ActionContext<R> context);
    Step<R> onCancel(ActionContext<R> context);

    class InitializationStep<R> implements Step<R> {

        @Override
        public boolean canRunInDisabled() {
            return false;
        }

        @Override
        public Step<R> execute(ActionContext<R> context) {
            context.initializeAction();

            if (context.isActionFinished()) {
                context.saveActionResult();
                return null;
            }

            return new ExecutionStep<>();
        }

        @Override
        public Step<R> onError(ActionContext<R> context) {
            return null;
        }

        @Override
        public Step<R> onCancel(ActionContext<R> context) {
            return null;
        }
    }

    class ExecutionStep<R> implements Step<R> {

        @Override
        public boolean canRunInDisabled() {
            return false;
        }

        @Override
        public Step<R> execute(ActionContext<R> context) {
            if (context.isActionTimeout()) {
                context.interruptAction();
                return new EndStep<>();
            }

            context.executeAction();

            if (context.isActionFinished()) {
                context.saveActionResult();
                return new EndStep<>();
            }

            return this;
        }

        @Override
        public Step<R> onError(ActionContext<R> context) {
            context.interruptAction();
            return new EndStep<>();
        }

        @Override
        public Step<R> onCancel(ActionContext<R> context) {
            context.interruptAction();
            return new EndStep<>();
        }
    }

    class EndStep<R> implements Step<R> {

        @Override
        public boolean canRunInDisabled() {
            return true;
        }

        @Override
        public Step<R> execute(ActionContext<R> context) {
            context.endAction();
            return null;
        }

        @Override
        public Step<R> onError(ActionContext<R> context) {
            return null;
        }

        @Override
        public Step<R> onCancel(ActionContext<R> context) {
            return null;
        }
    }
}
