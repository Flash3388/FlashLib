package com.flash3388.flashlib.robot.base.generic;

@FunctionalInterface
public interface Initializer {

    Object initialize(DependencyHolder dependencyHolder);
}
