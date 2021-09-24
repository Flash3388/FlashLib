package com.flash3388.flashlib.scheduling2.actions;

public interface Step {

    boolean canRunInDisabled();

    Step execute(ActionContext context);
    Step onError(ActionContext context);
    Step onCancel(ActionContext context);

    class InitializationStep implements Step {

        @Override
        public boolean canRunInDisabled() {
            return false;
        }

        @Override
        public Step execute(ActionContext context) {
            context.initializeAction();

            if (context.isActionFinished()) {
                context.actionFinished();
                return null;
            }

            return new ExecutionStep();
        }

        @Override
        public Step onError(ActionContext context) {
            return null;
        }

        @Override
        public Step onCancel(ActionContext context) {
            return null;
        }
    }

    class ExecutionStep implements Step {

        @Override
        public boolean canRunInDisabled() {
            return false;
        }

        @Override
        public Step execute(ActionContext context) {
            if (context.isActionTimeout()) {
                context.interruptAction();
                return new EndStep();
            }

            context.executeAction();

            if (context.isActionFinished()) {
                context.actionFinished();
                return new EndStep();
            }

            return this;
        }

        @Override
        public Step onError(ActionContext context) {
            context.interruptAction();
            return new EndStep();
        }

        @Override
        public Step onCancel(ActionContext context) {
            context.interruptAction();
            return new EndStep();
        }
    }

    class EndStep implements Step {

        @Override
        public boolean canRunInDisabled() {
            return true;
        }

        @Override
        public Step execute(ActionContext context) {
            context.endAction();
            return null;
        }

        @Override
        public Step onError(ActionContext context) {
            return null;
        }

        @Override
        public Step onCancel(ActionContext context) {
            return null;
        }
    }
}
