package com.flash3388.flashlib.robot.base.generic;

import java.util.Collection;

public class CascadingInitializer {

    private final DependencyHolder mDependencyHolder;
    private final Collection<Initializer> mInitializers;

    public CascadingInitializer(DependencyHolder dependencyHolder, Collection<Initializer> initializers) {
        mDependencyHolder = dependencyHolder;
        mInitializers = initializers;
    }

    public CascadingInitializer(Collection<Initializer> initializers, Collection<Object> existingDependencies) {
        this(new DependencyHolder(), initializers);
        mDependencyHolder.addAll(existingDependencies);
    }

    public DependencyHolder initialize() {
        for (Initializer initializer : mInitializers) {
            Object object = initializer.initialize(mDependencyHolder);
            mDependencyHolder.add(object);
        }

        return mDependencyHolder;
    }
}
