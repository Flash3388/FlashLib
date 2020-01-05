package com.flash3388.flashlib.io;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CloserTest {

    @Test
    public void run_closeAlwaysNoError_closesAll() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        closer.run(mock(IoRunnable.class), CloseOption.CLOSE_ALWAYS);

        verify(mockCloseable, times(1)).close();
    }

    @Test
    public void run_closeAlwaysErrorOccurredInRunnable_closesAllAndThrows() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        IoRunnable runnable = mockThrowingRunnable();
        try {
            closer.run(runnable, CloseOption.CLOSE_ALWAYS);
            fail("did not throw");
        } catch (IOException expected) {
            // expected
        }

        verify(mockCloseable, times(1)).close();
    }

    @Test
    public void run_closeOnErrorErrorOccurred_closes() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        IoRunnable runnable = mockThrowingRunnable();
        try {
            closer.run(runnable, CloseOption.CLOSE_ON_ERROR);
            fail("did not throw");
        } catch (IOException expected) {
            // expected
        }

        verify(mockCloseable, times(1)).close();
    }

    @Test
    public void run_closeOnErrorNoError_doesNotClose() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        closer.run(mock(IoRunnable.class), CloseOption.CLOSE_ON_ERROR);

        verify(mockCloseable, times(0)).close();
    }

    @Test
    public void run_closeOnSuccessNoError_closes() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        closer.run(mock(IoRunnable.class), CloseOption.CLOSE_ON_SUCCESS);

        verify(mockCloseable, times(1)).close();
    }

    @Test
    public void run_closeOnErrorErrorOccurred_doesNotClose() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mock(Closeable.class);
        closer.add(mockCloseable);

        IoRunnable runnable = mockThrowingRunnable();
        try {
            closer.run(runnable, CloseOption.CLOSE_ON_SUCCESS);
            fail("did not throw");
        } catch (IOException expected) {
            // expected
        }

        verify(mockCloseable, times(0)).close();
    }

    @Test
    public void run_errorOccurredInRunnableAndClose_suppressesCloseException() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseable = mockThrowingCloseable();
        closer.add(mockCloseable);

        IoRunnable runnable = mockThrowingRunnable();
        try {
            closer.run(runnable);
            fail("did not throw");
        } catch (IOException expected) {
            // expected
            assertHasSuppressed(expected);
        }
    }

    @Test
    public void close_errorOccurredOnClose_closesAllAndThrows() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseableThrowing = mockThrowingCloseable();
        closer.add(mockCloseableThrowing);

        Closeable mockCloseableNotThrowing = mock(Closeable.class);
        closer.add(mockCloseableNotThrowing);

        try {
            closer.close();
            fail("did not throw");
        } catch (IOException expected) {
            // expected
        }

        verify(mockCloseableThrowing, times(1)).close();
        verify(mockCloseableNotThrowing, times(1)).close();
    }

    @Test
    public void close_errorOccurredOnMultipleClose_throwsAndSuppresses() throws Exception {
        Closer closer = Closer.empty();

        Closeable mockCloseableThrowing = mockThrowingCloseable();
        closer.add(mockCloseableThrowing);

        Closeable mockCloseableThrowing2 = mockThrowingCloseable();
        closer.add(mockCloseableThrowing2);

        try {
            closer.close();
            fail("did not throw");
        } catch (IOException expected) {
            // expected
            assertHasSuppressed(expected);

        }
    }

    private void assertHasSuppressed(Throwable throwable) {
        assertEquals(1, throwable.getSuppressed().length);
    }

    private IoRunnable mockThrowingRunnable() throws IOException {
        IoRunnable mockRunnable = mock(IoRunnable.class);
        when(mockRunnable.run()).thenThrow(new IOException());
        
        return mockRunnable;
    }

    private Closeable mockThrowingCloseable() throws IOException {
        Closeable mockCloseableThrowing = mock(Closeable.class);
        doThrow(new IOException()).when(mockCloseableThrowing).close();

        return mockCloseableThrowing;
    }
}