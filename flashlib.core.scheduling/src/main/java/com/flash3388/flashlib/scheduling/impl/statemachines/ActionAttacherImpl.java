package com.flash3388.flashlib.scheduling.impl.statemachines;

import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.statemachines.ActionAttacher;
import com.flash3388.flashlib.scheduling.statemachines.StateConfigurer;
import com.flash3388.flashlib.scheduling.statemachines.Transition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class ActionAttacherImpl implements ActionAttacher {

    private final StateConfigurerImpl mConfigurer;
    private final StateContainer mContainer;
    private final Supplier<Action> mActionSupplier;

    private final Collection<AttachmentTransition> mTransitions;

    public ActionAttacherImpl(StateConfigurerImpl configurer,
                              StateContainer container,
                              Supplier<Action> actionSupplier) {
        mConfigurer = configurer;
        mContainer = container;
        mActionSupplier = actionSupplier;

        mTransitions = new ArrayList<>();
    }

    @Override
    public ActionAttacher onFinishTransition(Transition transition) {
        AttachmentTransition attachmentTransition = new AttachmentTransition(transition);
        mTransitions.add(attachmentTransition);

        return this;
    }

    @Override
    public ActionAttacher onFinishTransition(Transition transition, Set<FinishReason> reasons) {
        Objects.requireNonNull(reasons, "reasons is null");

        if (reasons.size() == 0) {
            return onFinishTransition(transition);
        }

        AttachmentTransition attachmentTransition = new AttachmentTransition(transition, reasons);
        mTransitions.add(attachmentTransition);

        return this;
    }

    @Override
    public StateConfigurer done() {
        mContainer.addAction(new AttachedActionSupplier(mActionSupplier, mTransitions));
        return mConfigurer;
    }
}
