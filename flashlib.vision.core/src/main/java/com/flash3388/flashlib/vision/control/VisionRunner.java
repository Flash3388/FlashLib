package com.flash3388.flashlib.vision.control;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.vision.analysis.Analysis;

import java.util.function.Consumer;
import java.util.function.Function;

public class VisionRunner extends TerminalServiceBase {

    private final Function<Consumer<Analysis>, AutoCloseable> mTaskRunner;
    private final Consumer<Analysis> mAnalysisConsumer;
    private AutoCloseable mRunner;

    public VisionRunner(Function<Consumer<Analysis>, AutoCloseable> taskRunner,
                        Consumer<Analysis> analysisConsumer) {
        mTaskRunner = taskRunner;
        mAnalysisConsumer = analysisConsumer;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mRunner = mTaskRunner.apply(mAnalysisConsumer);
    }

    @Override
    protected void stopRunning() {
        if (mRunner != null) {
            try {
                mRunner.close();
            } catch (Exception e) {
                // TODO HANDLE
            }
        }
    }
}
