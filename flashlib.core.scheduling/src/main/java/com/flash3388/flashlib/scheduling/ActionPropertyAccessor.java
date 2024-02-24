package com.flash3388.flashlib.scheduling;

/**
 * Allows access to properties associated with an action. Each action will have its own
 * set of associated properties.
 *
 * @since FlashLib 3.3.0
 */
public interface ActionPropertyAccessor { //TODO: UPDATE SINCE TO RIGHT VERSION

    /**
     * Gets a typed-property with a specified name.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param defaultValue value to return if the property wasn't created
     *
     * @return value associated with the name, or <em>defaultValue</em> if no property exists for the given name
     */
    boolean getBooleanProperty(String name, boolean defaultValue);

    /**
     * Gets a typed-property with a specified name.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param defaultValue value to return if the property wasn't created
     *
     * @return value associated with the name, or <em>defaultValue</em> if no property exists for the given name
     */
    int getIntProperty(String name, int defaultValue);

    /**
     * Gets a typed-property with a specified name.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param defaultValue value to return if the property wasn't created
     *
     * @return value associated with the name, or <em>defaultValue</em> if no property exists for the given name
     */
    long getLongProperty(String name, long defaultValue);

    /**
     * Gets a typed-property with a specified name.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param defaultValue value to return if the property wasn't created
     *
     * @return value associated with the name, or <em>defaultValue</em> if no property exists for the given name
     */
    double getDoubleProperty(String name, double defaultValue);

    /**
     * Gets a typed-property with a specified name.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param defaultValue value to return if the property wasn't created
     *
     * @return value associated with the name, or <em>defaultValue</em> if no property exists for the given name
     */
    String getStringProperty(String name, String defaultValue);

    /**
     * Puts a typed-property with a specified name and value.
     * If the property does not exist, it is created, otherwise its value
     * is updated.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param value value to set
     */
    void putBooleanProperty(String name, boolean value);

    /**
     * Puts a typed-property with a specified name and value.
     * If the property does not exist, it is created, otherwise its value
     * is updated.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param value value to set
     */
    void putIntProperty(String name,  int value);

    /**
     * Puts a typed-property with a specified name and value.
     * If the property does not exist, it is created, otherwise its value
     * is updated.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param value value to set
     */
    void putLongProperty(String name, long value);

    /**
     * Puts a typed-property with a specified name and value.
     * If the property does not exist, it is created, otherwise its value
     * is updated.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param value value to set
     */
    void putDoubleProperty(String name, double value);

    /**
     * Puts a typed-property with a specified name and value.
     * If the property does not exist, it is created, otherwise its value
     * is updated.
     *
     * Use this to debug, track data, or control the execution of your action.
     *
     * @param name name of the property
     * @param value value to set
     */
    void putStringProperty(String name, String value);
}
