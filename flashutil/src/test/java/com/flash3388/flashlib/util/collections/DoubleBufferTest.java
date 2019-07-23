package com.flash3388.flashlib.util.collections;

import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static org.junit.Assert.*;

public class DoubleBufferTest {

    @Test
    public void write_ofObject_writesObjectToWriteIndex() throws Exception {
        final Object VALUE = new Object();

        AtomicReferenceArray<Object> innerArray = new AtomicReferenceArray<>(2);
        DoubleBuffer<Object> doubleBuffer = new DoubleBuffer<>(innerArray, new AtomicInteger(0));

        doubleBuffer.write(VALUE);

        assertEquals(VALUE, innerArray.get(0));
    }

    @Test
    public void write_ofObject_swapsWriteIndex() throws Exception {
        final Object VALUE = new Object();

        AtomicReferenceArray<Object> innerArray = new AtomicReferenceArray<>(2);
        AtomicInteger readIndex = new AtomicInteger(0);
        DoubleBuffer<Object> doubleBuffer = new DoubleBuffer<>(innerArray, readIndex);

        doubleBuffer.write(VALUE);

        assertEquals(1, readIndex.get());
    }

    @Test(expected = NoSuchElementException.class)
    public void read_initialValues_throwsNoSuchElementException() throws Exception {
        AtomicReferenceArray<Object> innerArray = new AtomicReferenceArray<>(2);
        DoubleBuffer<Object> doubleBuffer = new DoubleBuffer<>(innerArray, new AtomicInteger(0));

        doubleBuffer.read();
    }

    @Test
    public void read_valueExistsInArray_returnsThatValue() throws Exception {
        final Object VALUE = new Object();

        AtomicReferenceArray<Object> innerArray = new AtomicReferenceArray<>(2);
        innerArray.set(0, VALUE);

        DoubleBuffer<Object> doubleBuffer = new DoubleBuffer<>(innerArray, new AtomicInteger(0));

        Object read = doubleBuffer.read();
        assertEquals(VALUE, read);
    }

    @Test
    public void read_withIndexOnItem_swapsReadIndex() throws Exception {
        final Object VALUE = new Object();

        AtomicReferenceArray<Object> innerArray = new AtomicReferenceArray<>(2);
        innerArray.set(0, VALUE);

        AtomicInteger readIndex = new AtomicInteger(0);
        DoubleBuffer<Object> doubleBuffer = new DoubleBuffer<>(innerArray, readIndex);

        Object value = doubleBuffer.read();

        assertEquals(VALUE, value);
    }
}