package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.control.Invertable;
import com.flash3388.flashlib.robot.scheduling.Trigger;

import java.util.function.BooleanSupplier;

public interface Button extends BooleanSupplier, Invertable, Trigger {

}
