package com.flash3388.flashlib.app.watchdog;

import com.flash3388.flashlib.time.Time;

public class TimestampReport {

    private final String mKey;
    private final Time mReportTime;

    public TimestampReport(String key, Time reportTime) {
        mKey = key;
        mReportTime = reportTime;
    }

    public String getKey() {
        return mKey;
    }

    public Time getReportTime() {
        return mReportTime;
    }
}
