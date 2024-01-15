package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.List;

public class MultiFeedReporters implements FeedReporter {

    private final Collection<FeedReporter> mReporters;

    public MultiFeedReporters(Collection<FeedReporter> reporters) {
        mReporters = reporters;
    }

    @Override
    public void reportFeed(String watchdogName) {
        for (FeedReporter reporter : mReporters) {
            reporter.reportFeed(watchdogName);
        }
    }

    @Override
    public void reportFeedExpired(String watchdogName, Time feedOverrun, List<TimestampReport> reports) {
        for (FeedReporter reporter : mReporters) {
            reporter.reportFeedExpired(watchdogName, feedOverrun, reports);
        }
    }
}
