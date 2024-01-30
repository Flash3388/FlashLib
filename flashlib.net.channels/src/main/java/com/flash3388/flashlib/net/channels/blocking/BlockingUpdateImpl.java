package com.flash3388.flashlib.net.channels.blocking;

public class BlockingUpdateImpl<T extends Enum<T>, D> implements Blocking.Update<T, D> {

    public T mType;
    public D mData;

    @Override
    public boolean isEmpty() {
        return mType != null;
    }

    @Override
    public T getType() {
        return mType;
    }

    @Override
    public D getData() {
        return mData;
    }

    @Override
    public void done() {
        clear();
    }

    public void clear() {
        mType = null;
        mData = null;
    }

    public void loadChange(UpdateData<T, D> data) {
        mType = data.getType();
        mData = data.getData();
    }
}
