package com.flash3388.flashlib.scheduling.actions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Specialized selection algorithm for choosing an action based on an input value by conditions.
 *
 * @param <T> type of parameter which is fed into the selector
 *
 * @since FlashLib 3.2.0
 */
public class ActionStartSelector<T> {

    /**
     * Builder for {@link ActionStartSelector}
     *
     * @param <T> type of parameter which is fed into the selector
     */
    public static class Builder<T> {

        private final List<ConditionNode<T>> mNodes;
        private Function<T, Action> mDefaultAction;

        public Builder() {
            mNodes = new LinkedList<>();
        }

        /**
         * Create a condition which, when met, will lead to a selection of t he given action.
         *
         * @param actionProvider creator of the wanted action
         * @param condition condition to check the input value
         * @return this
         */
        public Builder<T> useWhen(Function<T, Action> actionProvider, Predicate<T> condition) {
            mNodes.add(new ConditionNode<>(actionProvider, condition));
            return this;
        }

        /**
         * Set an action as the default selection if no condition was matched.
         *
         * @param actionProvider creator for the action
         * @return this
         */
        public Builder<T> useElse(Function<T, Action> actionProvider) {
            mDefaultAction = actionProvider;
            return this;
        }

        public ActionStartSelector<T> build() {
            return new ActionStartSelector<>(new ArrayList<>(mNodes), mDefaultAction);
        }
    }

    private static class ConditionNode<T> {
        public Function<T, Action> actionProvider;
        public Predicate<T> condition;

        ConditionNode(Function<T, Action> actionProvider, Predicate<T> condition) {
            this.actionProvider = actionProvider;
            this.condition = condition;
        }
    }

    private final List<ConditionNode<T>> mNodes;
    private final Function<T, Action> mDefaultAction;

    private ActionStartSelector(List<ConditionNode<T>> nodes, Function<T, Action> defaultAction) {
        mNodes = nodes;
        mDefaultAction = defaultAction;
    }

    /**
     * Creates an action based on the given value.
     * A list of pre-created conditions are checked and the first matching condition is used to provide
     * the matching action.
     * If no condition was met, the default action is selected. If no default action exists, an exception
     * is raised.
     *
     * @param value value used to select the action.
     * @return action selected.
     * @throws NoSuchElementException if no condition is met and no default action exists.
     */
    public Action create(T value) {
        for (ConditionNode<T> node : mNodes) {
            if (node.condition.test(value)) {
                return node.actionProvider.apply(value);
            }
        }

        if (mDefaultAction != null) {
            return mDefaultAction.apply(value);
        }

        throw new NoSuchElementException("No handlers for value " + value);
    }
}
