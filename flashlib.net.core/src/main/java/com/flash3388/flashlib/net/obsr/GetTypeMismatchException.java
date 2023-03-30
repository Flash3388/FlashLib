package com.flash3388.flashlib.net.obsr;

public class GetTypeMismatchException extends RuntimeException {

    public GetTypeMismatchException(ValueType wantedType, ValueType actualType) {
        super(String.format("type %s wanted, but actual type is %s", wantedType, actualType));
    }
}
