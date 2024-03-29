package com.flash3388.flashlib.vision.processing;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProcessorChainTest {

    @Test
    public void process_withTwoProcessors_processesInAndPipesToOut() throws Exception {
        final Object INPUT = new Object();
        final Object OUTPUT_IN = new Object();

        final Processor<Object, Object> PROCESSOR_IN = mockProcessorWithInputOutput(INPUT, OUTPUT_IN);
        final Processor<Object, Object> PROCESSOR_OUT = mock(Processor.class);

        ProcessorChain processorChain = new ProcessorChain(PROCESSOR_IN, PROCESSOR_OUT);
        processorChain.process(INPUT);

        verify(PROCESSOR_IN, times(1)).process(eq(INPUT));
        verify(PROCESSOR_OUT, times(1)).process(eq(OUTPUT_IN));
    }

    @Test
    public void process_withMultipleProcessors_processesThroughAll() throws Exception {
        final Object[] DATA = {
                new Object(),
                new Object(),
                new Object(),
                new Object()
        };
        final Processor[] PROCESSORS = new Processor[DATA.length - 1];
        for (int i = 0; i < PROCESSORS.length; i++) {
            PROCESSORS[i] = mockProcessorWithInputOutput(DATA[i], DATA[i+1]);
        }

        ProcessorChain processorChain = new ProcessorChain(PROCESSORS);
        processorChain.process(DATA[0]);

        for (int i = 0; i < PROCESSORS.length; i++) {
            verify(PROCESSORS[i], times(1)).process(eq(DATA[i]));
        }
    }

    @Test
    public void process_withTypedProcessors_producesCorrectEndResult() throws Exception {
        final Processor[] PROCESSORS = {
                (Processor<Object, Integer>) Object::hashCode,
                (Processor<Integer, String>) String::valueOf,
                (Processor<String, Character>) input -> input.charAt(0)
        };

        final Object INPUT = new Object();
        final Object EXPECTED_RESULT = String.valueOf(INPUT.hashCode()).charAt(0);

        ProcessorChain processorChain = new ProcessorChain(PROCESSORS);
        Object output = processorChain.process(INPUT);

        assertThat(output, equalTo(EXPECTED_RESULT));
    }

    private Processor<Object, Object> mockProcessorWithInputOutput(Object input, Object output) throws Exception {
        final Processor<Object, Object> processor = mock(Processor.class);
        when(processor.process(eq(input))).thenReturn(output);
        return processor;
    }
}