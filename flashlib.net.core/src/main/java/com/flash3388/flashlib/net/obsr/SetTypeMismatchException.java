package com.flash3388.flashlib.net.obsr;

public class SetTypeMismatchException extends RuntimeException {

    public SetTypeMismatchException(ValueType type, Class<?> expected, Class<?> actual) {
        super(String.format("type %s expects %s, but received %s", type.name(), expected.getName(), actual.getName()));
    }

    public SetTypeMismatchException(ValueType type, String message) {
        super(String.format("type %s %s", type, message));
    }
}
