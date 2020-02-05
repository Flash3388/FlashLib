package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Subsystem;
import org.mockito.stubbing.Answer;

import java.util.Collection;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ActionsMock {

    private ActionsMock() {
    }

    public static class ActionMocker {

        private ActionConfiguration mConfiguration;
        private boolean mIsFinished;
        private boolean mRunWhenDisabled;

        private ActionMocker() {
            mConfiguration = new ActionConfiguration();
            mIsFinished = false;
            mRunWhenDisabled = false;
        }

        public ActionMocker withConfiguration(ActionConfiguration configuration) {
            mConfiguration = configuration;
            return this;
        }

        public ActionMocker withRequirements(Collection<? extends Subsystem> requirements) {
            mConfiguration.requires(new HashSet<>(requirements));
            return this;
        }

        public ActionMocker isFinished(boolean isFinished) {
            mIsFinished = isFinished;
            return this;
        }

        public ActionMocker runWhenDisabled(boolean runWhenDisabled) {
            mRunWhenDisabled = runWhenDisabled;
            return this;
        }

        public Action build() {
            Action action = mock(Action.class);
            when(action.runWhenDisabled()).thenReturn(mRunWhenDisabled);
            when(action.isFinished()).thenReturn(mIsFinished);
            when(action.getConfiguration()).thenReturn(mConfiguration);
            when(action.configure()).thenAnswer(invocation -> new ActionConfiguration.Editor(action, action.getConfiguration()));

            doAnswer((Answer<Void>) invocation -> {
                ActionConfiguration configuration = invocation.getArgument(0);
                when(action.getConfiguration()).thenReturn(configuration);
                return null;
            }).when(action).setConfiguration(any(ActionConfiguration.class));

            return action;
        }
    }

    public static ActionMocker actionMocker() {
        return new ActionMocker();
    }

    public static class ContextMocker {

        private boolean mRunFinished;

        private ContextMocker() {
            mRunFinished = true;
        }

        public ContextMocker runFinished(boolean runFinished) {
            mRunFinished = runFinished;
            return this;
        }

        public ActionContext build() {
            ActionContext actionContext = mock(ActionContext.class);
            when(actionContext.run()).thenReturn(!mRunFinished);

            return actionContext;
        }
    }

    public static ContextMocker contextMocker() {
        return new ContextMocker();
    }

    public static Action makeActionCancelable(Action action) {
        /*doAnswer((Answer<Void>) invocation -> {
            when(action.isCanceled()).thenReturn(true);
            return null;
        }).when(action).markCanceled();*/

        return action;
    }
}
