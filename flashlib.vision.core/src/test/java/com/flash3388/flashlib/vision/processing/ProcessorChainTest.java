package com.flash3388.flashlib.vision.processing;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessorChainTest {

    @Test
    public void process_forInput_processesInAndPipesToOut() throws Exception {
        final Object INPUT = new Object();
        final Object OUTPUT_IN = new Object();

        final Processor<Object, Object> PROCESSOR_IN = mock(Processor.class);
        when(PROCESSOR_IN.process(eq(INPUT))).thenReturn(OUTPUT_IN);

        final Processor<Object, Object> PROCESSOR_OUT = mock(Processor.class);

        ProcessorChain<Object, Object, Object> processorChain = new ProcessorChain<>(PROCESSOR_IN, PROCESSOR_OUT);
        processorChain.process(INPUT);

        verify(PROCESSOR_IN, times(1)).process(eq(INPUT));
        verify(PROCESSOR_OUT, times(1)).process(eq(OUTPUT_IN));
    }
}