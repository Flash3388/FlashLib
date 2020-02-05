package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.ActionContext;
import com.flash3388.flashlib.robot.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.time.Clock;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsIterableContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ActionControlTest {

    private RequirementsControl mRequirementsControl;

    private Map<Action, ActionContext> mRunningActions;
    private Collection<Action> mNextRunActions;

    private ActionControl mActionControl;

    @BeforeEach
    public void setup() {
        mRequirementsControl = mock(RequirementsControl.class);
        mRunningActions = new HashMap<>();
        mNextRunActions = new ArrayList<>();

        mActionControl = new ActionControl(mock(Clock.class), mRequirementsControl, mRunningActions, mNextRunActions);
    }

    @Test
    public void startAction_actionNotRunning_addsToRunning() throws Exception {
        Action action = mock(Action.class);
        mActionControl.startAction(action);

        assertThat(mNextRunActions, IsIterableContaining.hasItem(action));
    }

    @Test
    public void startAction_actionRunning_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()-> {
            Action action = mock(Action.class);
            mRunningActions.put(action, mock(ActionContext.class));

            mActionControl.startAction(action);
        });
    }

    @Test
    public void startAction_actionRunForNext_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()-> {
            Action action = mock(Action.class);
            mNextRunActions.add(action);

            mActionControl.startAction(action);
        });
    }

    @Test
    public void cancelAction_actionRunning_cancels() throws Exception {
        Action action = mock(Action.class);
        ActionContext context = mock(ActionContext.class);
        mRunningActions.put(action, context);

        mActionControl.cancelAction(action);

        verify(context, times(1)).cancelAction();
    }

    @Test
    public void cancelAction_actionNotRunning_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()-> {
            Action action = mock(Action.class);

            mActionControl.cancelAction(action);
        });
    }

    @Test
    public void isRunning_actionNotRunning_returnsFalse() throws Exception {
        Action action = mock(Action.class);

        assertFalse(mActionControl.isActionRunning(action));
    }

    @Test
    public void isRunning_actionRunning_returnsTrue() throws Exception {
        Action action = mock(Action.class);
        mRunningActions.put(action, mock(ActionContext.class));

        assertTrue(mActionControl.isActionRunning(action));
    }

    @Test
    public void updateActionsForNextRun_withActionsToRemove_runMarkedFinished() throws Exception {
        Map<Action, ActionContext> actions = new HashMap<>();
        actions.put(mock(Action.class), mock(ActionContext.class));
        actions.put(mock(Action.class), mock(ActionContext.class));

        mRunningActions.putAll(actions);

        mActionControl.updateActionsForNextRun(actions.keySet());

        actions.values().forEach((ctx)-> verify(ctx, times(1)).runFinished());
    }

    @Test
    public void updateActionsForNextRun_withActionsToRemove_requirementsMarkedNoAction() throws Exception {
        Map<Action, ActionContext> actions = new HashMap<>();
        actions.put(mock(Action.class), mock(ActionContext.class));
        actions.put(mock(Action.class), mock(ActionContext.class));

        mRunningActions.putAll(actions);

        mActionControl.updateActionsForNextRun(actions.keySet());

        ArgumentCaptor<Action> captor = ArgumentCaptor.forClass(Action.class);
        verify(mRequirementsControl, times(actions.size())).updateRequirementsNoCurrentAction(captor.capture());
        assertThat(captor.getAllValues(), IsIterableContainingInAnyOrder.containsInAnyOrder(actions.keySet().toArray()));
    }

    @Test
    public void updateActionsForNextRun_withActionsToStart_startsActions() throws Exception {
        Collection<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );
        mNextRunActions.addAll(actions);

        mActionControl.updateActionsForNextRun(Collections.emptyList());

        assertThat(mRunningActions.keySet(), IsIterableContainingInAnyOrder.containsInAnyOrder(actions.toArray()));
    }

    @Test
    public void updateActionsForNextRun_withActionsToStart_updatesRequirements() throws Exception {
        Collection<Action> actions = Arrays.asList(
                ActionsMock.actionMocker().build(),
                ActionsMock.actionMocker().build()
        );
        mNextRunActions.addAll(actions);

        mActionControl.updateActionsForNextRun(Collections.emptyList());

        ArgumentCaptor<Action> captor = ArgumentCaptor.forClass(Action.class);
        verify(mRequirementsControl, times(actions.size())).updateRequirementsWithNewRunningAction(captor.capture());
        assertThat(captor.getAllValues(), IsIterableContainingInAnyOrder.containsInAnyOrder(actions.toArray()));
    }

    @Test
    public void stopAllActions_withRunningActions_runMarkedFinished() throws Exception {
        Map<Action, ActionContext> actions = new HashMap<>();
        actions.put(mock(Action.class), mock(ActionContext.class));
        actions.put(mock(Action.class), mock(ActionContext.class));

        mRunningActions.putAll(actions);

        mActionControl.stopAllActions();

        actions.values().forEach((ctx)-> verify(ctx, times(1)).runFinished());
    }

    @Test
    public void stopAllActions_withRunningActions_requirementsMarkedNoAction() throws Exception {
        Map<Action, ActionContext> actions = new HashMap<>();
        actions.put(mock(Action.class), mock(ActionContext.class));
        actions.put(mock(Action.class), mock(ActionContext.class));

        mRunningActions.putAll(actions);

        mActionControl.stopAllActions();

        ArgumentCaptor<Action> captor = ArgumentCaptor.forClass(Action.class);
        verify(mRequirementsControl, times(actions.size())).updateRequirementsNoCurrentAction(captor.capture());
        assertThat(captor.getAllValues(), IsIterableContainingInAnyOrder.containsInAnyOrder(actions.keySet().toArray()));
    }
}