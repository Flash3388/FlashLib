package com.flash3388.flashlib.robot.base.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class DependencyHolder {

    private final Collection<Object> mDependencies;

    public DependencyHolder() {
        mDependencies = new ArrayList<>();
    }

    void add(Object dependency) {
        mDependencies.add(dependency);
    }
    void addAll(Collection<Object> dependencies) {
        mDependencies.addAll(dependencies);
    }

    public <T> T get(Class<T> type) {
        Optional<T> optional = tryGet(type);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new IllegalArgumentException("Dependency not found " + type);
    }

    public <T> Optional<T> tryGet(Class<T> type) {
        Object instance = null;
        for (Object dependency : mDependencies) {
            if (!type.isInstance(dependency)) {
                continue;
            }

            instance = dependency;
            break;
        }

        if (instance == null) {
            return Optional.empty();
        }

        return Optional.of(type.cast(instance));
    }

    public <T> Iterable<T> getAll(Class<T> type) {
        Collection<T> dependencies = new ArrayList<>();
        for (Object dependency : mDependencies) {
            if (!type.isInstance(dependency)) {
                continue;
            }

            dependencies.add(type.cast(dependency));
        }

        return dependencies;
    }
}
