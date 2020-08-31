package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.scheduling.Requirement;

public interface Valve extends Requirement {

    void open();
    void close();

    boolean isOpen();
}
