package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.ExecutionContext;
import com.flash3388.flashlib.time.Time;

public class TestActionControl implements ActionControl {
    @Override
    public ActionConfiguration getConfiguration() {
        return null;
    }

    @Override
    public Time getRunTime() {
        return null;
    }

    @Override
    public Time getTimeLeft() {
        return null;
    }

    @Override
    public ExecutionContext createExecutionContext(Action action) {
        return null;
    }

    @Override
    public void finish() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void putBooleanProperty(String name, boolean value) {

    }

    @Override
    public void putIntProperty(String name, int value) {

    }

    @Override
    public void putLongProperty(String name, long value) {

    }

    @Override
    public void putDoubleProperty(String name, double value) {

    }

    @Override
    public void putStringProperty(String name, String value) {

    }
}
