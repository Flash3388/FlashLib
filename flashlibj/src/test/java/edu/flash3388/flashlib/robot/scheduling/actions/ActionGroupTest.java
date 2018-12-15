package edu.flash3388.flashlib.robot.scheduling.actions;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import edu.flash3388.flashlib.robot.scheduling.Action;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActionGroupTest {

    @Test
    public void execute_sequentialExecution_executesActionsByOrder() throws Exception {
        BooleanProperty isFirstActionRunning = new SimpleBooleanProperty(true);

        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunningBySupplier(isFirstActionRunning));
        actions.add(mockActionNotRunning());

        ActionGroup actionGroup = new ActionGroup(ExecutionOrder.SEQUENTIAL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).start();
        verify(actions.get(1), times(0)).start();

        actionGroup.execute();

        verify(actions.get(1), times(0)).start();

        isFirstActionRunning.setAsBoolean(false);
        actionGroup.execute();
        actionGroup.execute();

        verify(actions.get(1), times(1)).start();

        assertTrue(actionGroup.isFinished());
    }

    @Test
    public void execute_parallelExecution_executesActionsInParallel() throws Exception {
        List<Action> actions = new ArrayList<>();
        actions.add(mockActionRunning());
        actions.add(mockActionRunning());

        ActionGroup actionGroup = new ActionGroup(ExecutionOrder.PARALLEL, actions);
        actionGroup.initialize();

        actionGroup.execute();

        verify(actions.get(0), times(1)).start();
        verify(actions.get(1), times(0)).start();

        actionGroup.execute();
    }

    private Action mockActionNotRunning() {
        Action action = mock(Action.class);
        when(action.isRunning()).thenReturn(false);

        return action;
    }

    private Action mockActionRunning() {
        Action action = mock(Action.class);
        when(action.isRunning()).thenReturn(true);

        return action;
    }

    private Action mockActionRunningBySupplier(BooleanSupplier supplier) {
        Action action = mock(Action.class);
        when(action.isRunning()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return supplier.getAsBoolean();
            }
        });

        return action;
    }
}