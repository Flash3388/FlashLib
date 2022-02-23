package com.flash3388.flashlib.scheduling2;

import com.flash3388.flashlib.time.Time;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public interface Configuration {

    String getName();
    void setName(String name);

    Set<Requirement> getRequirements();
    void requires(Collection<? extends Requirement> requirements);
    default void requires(Requirement... requirements) {
        requires(Arrays.asList(requirements));
    }

    Time getTimeout();
    void setTimeout(Time timeout);

    Set<ActionFlag> getFlags();
    void addFlags(Set<ActionFlag> flags);
    void removeFlags(Set<ActionFlag> flags);

    void copyTo(Configuration other);
}
