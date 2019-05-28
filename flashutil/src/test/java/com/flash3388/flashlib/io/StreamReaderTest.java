package com.flash3388.flashlib.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StreamReaderTest {

    @Test
    public void close_normal_closesUnderlyingStream() throws Exception {
        InputStream inputStream = mock(InputStream.class);

        StreamReader streamReader = new StreamReader(inputStream);
        streamReader.close();

        verify(inputStream, times(1)).close();
    }

    @Test
    public void readAll_normal_readsAllDataInStream() throws Exception {
        final byte[] DATA = {0x1, 0x2, 0x3, 0x1, 0xa, 0x3a, 0x3b};

        StreamReader streamReader = new StreamReader(new ByteArrayInputStream(DATA));
        byte[] readData = streamReader.readAll();

        assertArrayEquals(DATA, readData);
    }
}