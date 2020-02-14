package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Requirement;
import org.mockito.stubbing.Answer;

import java.util.Collection;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
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
        }

        public ActionMocker mockWithConfiguration(ActionConfiguration configuration) {
            mConfiguration = configuration;
            return this;
        }

        public ActionMocker mockWithRequirements(Collection<? extends Requirement> requirements) {
            mConfiguration.requires(new HashSet<>(requirements));
            return this;
        }

        public ActionMocker mockIsFinished(boolean isFinished) {
            mIsFinished = isFinished;
            return this;
        }

        public ActionMocker mockRunWhenDisabled(boolean runWhenDisabled) {
            mRunWhenDisabled = runWhenDisabled;
            return this;
        }

        public Action build() {
            Action action = mock(Action.class);
            when(action.isFinished()).thenReturn(mIsFinished);
            when(action.runWhenDisabled()).thenReturn(mRunWhenDisabled);
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

    public static ActionContext mockNonFinishingActionContext() {
        ActionContext actionContext = mock(ActionContext.class);
        when(actionContext.run()).thenReturn(true);

        return actionContext;
    }

    public static ActionContext mockFinishedActionContext() {
        ActionContext actionContext = mock(ActionContext.class);
        when(actionContext.run()).thenReturn(false);

        return actionContext;
    }

    public static Action mockNotAllowedInDisabledAction() {
        Action action = mock(Action.class);
        when(action.runWhenDisabled()).thenReturn(false);

        return action;
    }

    public static Action mockActionIsFinishedMarkedTrue() {
        Action action = mock(Action.class);
        when(action.isFinished()).thenReturn(true);

        return action;
    }

    public static Action mockThrowingAction() {
        Action action = mock(Action.class);
        doThrow(new RuntimeException()).when(action).execute();

        return action;
    }
}
