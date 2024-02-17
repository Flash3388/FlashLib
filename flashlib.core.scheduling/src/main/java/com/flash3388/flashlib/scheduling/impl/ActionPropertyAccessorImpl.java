package com.flash3388.flashlib.scheduling.impl;

import com.flash3388.flashlib.scheduling.ActionPropertyAccessor;

public class ActionPropertyAccessorImpl implements ActionPropertyAccessor {

    private final ObsrActionContext mObsrActionContext;

    public ActionPropertyAccessorImpl(ObsrActionContext obsrActionContext) {
        mObsrActionContext = obsrActionContext;
    }

    @Override
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        verifyContextActive();
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getBoolean(defaultValue);
    }

    @Override
    public int getIntProperty(String name, int defaultValue) {
        verifyContextActive();
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getInt(defaultValue);
    }

    @Override
    public long getLongProperty(String name, long defaultValue) {
        verifyContextActive();
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getLong(defaultValue);
    }

    @Override
    public double getDoubleProperty(String name, double defaultValue) {
        verifyContextActive();
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getDouble(defaultValue);
    }

    @Override
    public String getStringProperty(String name, String defaultValue) {
        verifyContextActive();
        return mObsrActionContext.getPropertiesRoot().getEntry(name).getString(defaultValue);
    }

    @Override
    public void putBooleanProperty(String name, boolean value) {
        verifyContextActive();
        mObsrActionContext.getPropertiesRoot().getEntry(name).setBoolean(value);
    }

    @Override
    public void putIntProperty(String name, int value) {
        verifyContextActive();
        mObsrActionContext.getPropertiesRoot().getEntry(name).setInt(value);
    }

    @Override
    public void putLongProperty(String name, long value) {
        verifyContextActive();
        mObsrActionContext.getPropertiesRoot().getEntry(name).setLong(value);
    }

    @Override
    public void putDoubleProperty(String name, double value) {
        verifyContextActive();
        mObsrActionContext.getPropertiesRoot().getEntry(name).setDouble(value);
    }

    @Override
    public void putStringProperty(String name, String value) {
        verifyContextActive();
        mObsrActionContext.getPropertiesRoot().getEntry(name).setString(value);
    }

    private void verifyContextActive() {
        if (!mObsrActionContext.isActive()) {
            throw new IllegalStateException("properties context no longer exists");
        }
    }
}
