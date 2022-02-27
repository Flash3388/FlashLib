package com.flash3388.flashlib.statemachine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class TransitionConfigEditor {

    public static class ConfigContext {

        private final TransitionConfigEditor mCaller;
        private final TransitionConfig mTransitionConfig;
        private final Set<State> mWhen;

        public ConfigContext(TransitionConfigEditor caller, TransitionConfig transitionConfig, Set<State> when) {
            mCaller = caller;
            mTransitionConfig = transitionConfig;
            mWhen = when;
        }

        public TransitionConfigEditor to(State... state) {
            mTransitionConfig.addTransition(mWhen, new LinkedHashSet<>(Arrays.asList(state)));
            return mCaller;
        }
    }

    private final TransitionConfig mTransitionConfig;

    public TransitionConfigEditor(TransitionConfig transitionConfig) {
        mTransitionConfig = transitionConfig;
    }

    public ConfigContext when(State... active) {
        return new ConfigContext(this, mTransitionConfig,
                new HashSet<>(Arrays.asList(active)));
    }
}
