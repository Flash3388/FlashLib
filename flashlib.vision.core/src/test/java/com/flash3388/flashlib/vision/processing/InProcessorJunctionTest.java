package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class InProcessorJunctionTest {

    @Test
    public void process_forInput_processesAndPipesInputToPipeline() throws Exception {
        final Object INPUT = new Object();

        final Processor<Object, Object> PROCESSOR = mock(Processor.class);
        final Pipeline<Object> PIPELINE = mock(Pipeline.class);

        InProcessorJunction<Object, Object> inProcessorJunction = new InProcessorJunction<>(PROCESSOR, PIPELINE);
        inProcessorJunction.process(INPUT);

        verify(PROCESSOR, times(1)).process(eq(INPUT));
        verify(PIPELINE, times(1)).process(eq(INPUT));
    }
}