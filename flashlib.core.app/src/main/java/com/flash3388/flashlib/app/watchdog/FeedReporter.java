package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;

import java.util.List;

public interface FeedReporter {

    void reportFeed(String watchdogName);
    void reportFeedExpired(String watchdogName, Time feedOverrun, List<TimestampReport> reports);
}
