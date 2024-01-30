package com.flash3388.flashlib.net.channels.blocking;

public class UpdateData<T extends Enum<T>, D> {

    private final T mType;
    private final D mData;

    public UpdateData(T type, D data) {
        mType = type;
        mData = data;
    }

    public T getType() {
        return mType;
    }

    public D getData() {
        return mData;
    }
}
