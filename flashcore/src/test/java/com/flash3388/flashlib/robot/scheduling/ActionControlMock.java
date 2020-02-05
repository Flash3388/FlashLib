package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.time.Clock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionControlMock {

    private final ActionControl mMock;
    private final Map<Action, ActionContext> mRunningActions;

    public ActionControlMock(ActionControl actionControl) {
        mMock = actionControl;
        mRunningActions = new HashMap<>();

        when(actionControl.getRunningActionContexts()).thenReturn(mRunningActions.entrySet());
        doAnswer((Answer<Void>) invocation -> {
            Action action = invocation.getArgument(0);
            mRunningActions.put(action, new ActionContext(action, mock(Clock.class)));
            return null;
        }).when(actionControl).startAction(any(Action.class));
    }

    public Action runningAction(ActionContext actionContext) {
        Action action = mock(Action.class);
        mRunningActions.put(action, actionContext);

        return action;
    }

    public ActionContext runningAction(Action action) {
        ActionContext actionContext = mock(ActionContext.class);
        mRunningActions.put(action, actionContext);

        return actionContext;
    }

    public ActionControl verify(VerificationMode verificationMode) {
        return Mockito.verify(mMock, verificationMode);
    }
}
