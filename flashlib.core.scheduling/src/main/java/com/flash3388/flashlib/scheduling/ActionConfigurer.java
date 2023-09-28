package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.time.Time;

public interface ActionConfigurer {

    ActionConfigurer requires(Requirement... requirements);
    ActionConfigurer setTimeout(Time timeout);
    ActionConfigurer setName(String name);
    ActionConfigurer addFlags(ActionFlag... flags);
}
