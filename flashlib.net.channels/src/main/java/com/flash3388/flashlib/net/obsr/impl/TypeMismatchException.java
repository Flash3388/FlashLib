package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryValueType;

public class TypeMismatchException extends RuntimeException {

    public TypeMismatchException(EntryValueType type, Class<?> expected, Class<?> actual) {
        super(String.format("type %s expects %s, but received %s", type.name(), expected.getName(), actual.getName()));
    }

    public TypeMismatchException(EntryValueType type, String message) {
        super(String.format("type %s %s", type, message));
    }
}
