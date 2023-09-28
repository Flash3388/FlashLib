package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public interface ActionBuilder {

    ActionBuilder requires(Requirement... requirements);
    ActionBuilder withTimeout(Time timeout);
    ActionBuilder withName(String name);
    ActionBuilder withFlags(ActionFlag... flags);

    Action build();
}
