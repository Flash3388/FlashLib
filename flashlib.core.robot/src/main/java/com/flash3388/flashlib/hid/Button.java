package com.flash3388.flashlib.hid;

import com.flash3388.flashlib.control.Invertable;
import com.flash3388.flashlib.scheduling.Trigger;

import java.util.function.BooleanSupplier;

public interface Button extends BooleanSupplier, Invertable, Trigger {

}
