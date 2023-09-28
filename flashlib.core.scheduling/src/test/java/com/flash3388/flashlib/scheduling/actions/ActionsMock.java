package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionConfiguration;
import com.flash3388.flashlib.scheduling.ActionFlag;
import com.flash3388.flashlib.scheduling.ActionInterface;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.Requirement;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collection;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public final class ActionsMock {

    private ActionsMock() {
    }

    public static class ActionMocker {

        private ActionConfiguration mConfiguration;
        private boolean mIsFinished;
        private boolean mIsRunning;

        private ActionMocker() {
            mConfiguration = new ActionConfiguration();
            mIsFinished = false;
            mIsRunning = false;
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
            if (runWhenDisabled) {
                mConfiguration.addFlags(ActionFlag.RUN_ON_DISABLED);
            } else {
                mConfiguration.removeFlags(ActionFlag.RUN_ON_DISABLED);
            }
            return this;
        }

        public ActionMocker mockIsRunning(boolean isRunning) {
            mIsRunning = isRunning;
            return this;
        }

        public ActionInterface build() {
            ActionInterface action = mock(ActionInterface.class);
            doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    ActionControl control = invocation.getArgument(0);
                    if (mIsFinished) {
                        control.finish();
                    }
                    return null;
                }
            }).when(action).execute(any(ActionControl.class));
            when(action.getConfiguration()).thenReturn(mConfiguration);
            when(action.isRunning()).thenReturn(mIsRunning);
            when(action.configure()).thenAnswer(invocation ->
                    new ActionConfiguration.Editor(action, action.getConfiguration()));

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
}
