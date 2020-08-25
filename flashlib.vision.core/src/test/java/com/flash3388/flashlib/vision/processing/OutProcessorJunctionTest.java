package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutProcessorJunctionTest {

    @Test
    public void process_forInput_processesAndPipesOutputToPipeline() throws Exception {
        final Object INPUT = new Object();
        final Object OUTPUT = new Object();

        final Processor<Object, Object> PROCESSOR = mock(Processor.class);
        when(PROCESSOR.process(eq(INPUT))).thenReturn(OUTPUT);

        final Pipeline<Object> PIPELINE = mock(Pipeline.class);

        OutProcessorJunction<Object, Object> outProcessorJunction = new OutProcessorJunction<>(PROCESSOR, PIPELINE);
        outProcessorJunction.process(INPUT);

        verify(PROCESSOR, times(1)).process(eq(INPUT));
        verify(PIPELINE, times(1)).process(eq(OUTPUT));
    }
}