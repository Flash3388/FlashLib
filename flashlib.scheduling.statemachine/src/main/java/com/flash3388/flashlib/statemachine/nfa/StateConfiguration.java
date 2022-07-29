package com.flash3388.flashlib.statemachine.nfa;

import com.flash3388.flashlib.statemachine.State;
import com.flash3388.flashlib.statemachine.nfa.attachments.ActionAttachment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StateConfiguration {

    private final State mState;
    private final Set<State> mCanTransitionTo;
    private final Set<ActionAttachment> mAttachedActions;

    public StateConfiguration(State state) {
        mState = state;
        mCanTransitionTo = new HashSet<>();
        mAttachedActions = new HashSet<>();
    }

    public void allowTransition(Collection<? extends State> states) {
        mCanTransitionTo.addAll(states);
    }

    public void attachAction(ActionAttachment actionAttachment) {
        mAttachedActions.add(actionAttachment);
    }

    public boolean canTransitionToAnyOf(Collection<? extends State> states) {
        for (State newState : states) {
            if (mCanTransitionTo.contains(newState)) {
                return true;
            }
        }

        return false;
    }

    public void onEnter() {
        for (ActionAttachment attachment : mAttachedActions) {
            attachment.onStateEnter();
        }
    }

    public void onExit() {
        for (ActionAttachment attachment : mAttachedActions) {
            attachment.onStateExit();
        }
    }
}
