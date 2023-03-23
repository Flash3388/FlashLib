package com.flash3388.flashlib.vision;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SyncPipeJunctionTest {

    @Test
    public void process_forInput_passesToAllPipes() throws Exception {
        final Pipeline[] PIPELINES = {
            mock(Pipeline.class),
            mock(Pipeline.class),
            mock(Pipeline.class)
        };
        final Object INPUT = new Object();

        SyncPipeJunction<Object> syncPipeJunction = new SyncPipeJunction<>(PIPELINES);
        syncPipeJunction.process(INPUT);

        for (Pipeline pipeline : PIPELINES) {
            verify(pipeline, times(1)).process(eq(INPUT));
        }
    }

    @Test
    public void divergeTo_forNewPipeline_addsPipelineToJunctionWithPreviousOnes() throws Exception {
        final Pipeline[] PIPELINES = {
                mock(Pipeline.class),
                mock(Pipeline.class),
                mock(Pipeline.class)
        };
        final Pipeline<? super Object> NEW_PIPELINE = mock(Pipeline.class);

        Collection<Pipeline> pipelines = new ArrayList<>(Arrays.asList(PIPELINES));

        SyncPipeJunction syncPipeJunction = new SyncPipeJunction(pipelines);
        syncPipeJunction.divergeTo(NEW_PIPELINE);

        assertThat(pipelines, containsInRelativeOrder(PIPELINES));
        assertThat(pipelines, hasItem(NEW_PIPELINE));
    }
}