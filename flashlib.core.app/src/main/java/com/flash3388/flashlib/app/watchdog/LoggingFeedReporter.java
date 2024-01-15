package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.List;

public class LoggingFeedReporter implements FeedReporter {

    private static final Logger LOGGER = Logging.getLogger("Watchdog");

    @Override
    public void reportFeed(String watchdogName) {
        // do not report
    }

    @Override
    public void reportFeedExpired(String watchdogName, Time feedOverrun, List<TimestampReport> reports) {
        StringBuilder builder = new StringBuilder();

        builder.append("Watchdog ").append(watchdogName).append(" overrun report\n");
        builder.append("Time overrun:\t").append(String.format("%.3fs\n", feedOverrun.valueAsSeconds()));
        builder.append("Timestamps:\n");

        for (TimestampReport report : reports) {
            builder.append('\t').append(report.getKey()).append(":")
                    .append(String.format("%.3fs\n", report.getReportTime().valueAsSeconds()));
        }

        LOGGER.warn(builder.toString());
    }
}
