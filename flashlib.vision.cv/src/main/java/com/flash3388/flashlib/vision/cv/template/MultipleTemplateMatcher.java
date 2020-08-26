package com.flash3388.flashlib.vision.cv.template;

import com.flash3388.flashlib.vision.cv.CvProcessing;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

public class MultipleTemplateMatcher implements TemplateMatcher {
    // TODO: CONSIDER USING A FORK-JOIN POOL TO SEPARATE WORK FROM matchWithScaling TASKS

    private final Collection<Mat> mTemplates;
    private final TemplateMatchingMethod mTemplateMatchingMethod;
    private final CvProcessing mCvProcessing;
    private final ExecutorService mExecutorService;

    public MultipleTemplateMatcher(Collection<Mat> templates, TemplateMatchingMethod templateMatchingMethod, CvProcessing cvProcessing, ExecutorService executorService) {
        mTemplates = templates;
        mTemplateMatchingMethod = templateMatchingMethod;
        mCvProcessing = cvProcessing;
        mExecutorService = executorService;
    }

    @Override
    public TemplateMatchingResult match(Mat scene) throws TemplateMatchingException {
        try {
            return runMatchOnTemplates((template) ->
                    new TemplateMatchingTask(
                            new SingleTemplateMatcher(template, mTemplateMatchingMethod, mCvProcessing),
                            scene));
        } catch (InterruptedException e) {
            throw new TemplateMatchingException(e);
        }
    }

    @Override
    public ScaledTemplateMatchingResult matchWithScaling(Mat scene, double initialScaleFactor) throws TemplateMatchingException {
        try {
            return runMatchOnTemplates((template) ->
                    new ScaledTemplateMatchingTask(
                            new SingleTemplateMatcher(template, mTemplateMatchingMethod, mCvProcessing),
                            scene,
                            initialScaleFactor));
        } catch (InterruptedException e) {
            throw new TemplateMatchingException(e);
        }
    }

    private <T extends TemplateMatchingResult> T runMatchOnTemplates(Function<Mat, Callable<T>> taskFromTemplate) throws InterruptedException, TemplateMatchingException {
        Collection<Future<T>> futures = new ArrayList<>();
        try {
            for (Mat template : mTemplates) {
                Callable<T> task = taskFromTemplate.apply(template);
                Future<T> future = mExecutorService.submit(task);
                futures.add(future);
            }

            return getBestMatch(futures);
        } finally {
            cancelRunningFutures(futures);
        }
    }

    private <T extends TemplateMatchingResult> T getBestMatch(Collection<Future<T>> futures) throws InterruptedException, TemplateMatchingException {
        T bestMatch = null;

        for (Future<T> future : futures) {
            try {
                T result = future.get();

                if (bestMatch == null || result.compareTo(bestMatch) > 0) {
                    bestMatch = result;
                }
            } catch (ExecutionException e) {
                throw new TemplateMatchingException(e);
            }
        }

        if (bestMatch == null) {
            throw new NoTemplateMatchException();
        }

        return bestMatch;
    }

    private <T extends TemplateMatchingResult> void cancelRunningFutures(Collection<Future<T>> futures) {
        for (Future<T> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }

    private static class TemplateMatchingTask implements Callable<TemplateMatchingResult> {

        private final TemplateMatcher mTemplateMatcher;
        private final Mat mScene;

        private TemplateMatchingTask(TemplateMatcher templateMatcher, Mat scene) {
            mTemplateMatcher = templateMatcher;
            mScene = scene;
        }

        @Override
        public TemplateMatchingResult call() throws Exception {
            return mTemplateMatcher.match(mScene);
        }
    }

    private static class ScaledTemplateMatchingTask implements Callable<ScaledTemplateMatchingResult> {

        private final TemplateMatcher mTemplateMatcher;
        private final Mat mScene;
        private final double mInitialScaleFactor;

        private ScaledTemplateMatchingTask(TemplateMatcher templateMatcher, Mat scene, double initialScaleFactor) {
            mTemplateMatcher = templateMatcher;
            mScene = scene;
            mInitialScaleFactor = initialScaleFactor;
        }

        @Override
        public ScaledTemplateMatchingResult call() throws Exception {
            return mTemplateMatcher.matchWithScaling(mScene, mInitialScaleFactor);
        }
    }
}
