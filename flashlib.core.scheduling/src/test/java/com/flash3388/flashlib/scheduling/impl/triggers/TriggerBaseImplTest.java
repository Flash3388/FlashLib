package com.flash3388.flashlib.scheduling.impl.triggers;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TriggerBaseImplTest {

    @Test
    public void setState_stateChanges_updatesListenersWithChange() throws Exception {
        TriggerStateListener listener = mock(TriggerStateListener.class);

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.INACTIVE);
        trigger.addStateListener(listener);

        trigger.setState(TriggerState.ACTIVE, mock(TriggerActionController.class));

        verify(listener, times(1)).onStateChange(
                eq(TriggerState.ACTIVE),
                eq(TriggerState.INACTIVE),
                any(TriggerActionController.class));
    }

    @Test
    public void whenActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.INACTIVE);
        trigger.whenActive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.ACTIVE, controller);

        assertThat(controller.getActionsToStartIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }

    @Test
    public void whenInactive_triggerDeactivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.ACTIVE);
        trigger.whenInactive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.INACTIVE, controller);

        assertThat(controller.getActionsToStartIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }

    @Test
    public void cancelWhenActive_triggerActivatesAndActionRunning_cancelsAction() throws Exception {
        Action mockAction = ActionsMock.actionMocker()
                .mockIsRunning(true)
                .build();

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.INACTIVE);
        trigger.cancelWhenActive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.ACTIVE, controller);

        assertThat(controller.getActionsToStopIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }

    @Test
    public void cancelWhenInactive_triggerDeactivatesAndActionRunning_cancelsAction() throws Exception {
        Action mockAction = ActionsMock.actionMocker()
                .mockIsRunning(true)
                .build();

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.ACTIVE);
        trigger.cancelWhenInactive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.INACTIVE, controller);

        assertThat(controller.getActionsToStopIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }

    @Test
    public void whileActive_triggerActivates_startAction() throws Exception {
        Action mockAction = mock(Action.class);

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.INACTIVE);
        trigger.whileActive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.ACTIVE, controller);

        assertThat(controller.getActionsToStartIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }

    @Test
    public void whileActive_triggerDeactivatesAndActionRunning_cancelsAction() throws Exception {
        Action mockAction = ActionsMock.actionMocker()
                .mockIsRunning(true)
                .build();

        TriggerBaseImpl trigger = new TriggerBaseImpl(TriggerState.ACTIVE);
        trigger.whileActive(mockAction);

        TriggerActionController controller = new TriggerActionController();
        trigger.setState(TriggerState.INACTIVE, controller);

        assertThat(controller.getActionsToStopIfRunning(), IsIterableContainingInOrder.contains(mockAction));
    }
}