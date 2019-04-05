package com.flash3388.flashlib.util.logging.jul;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class JsonFormatterTest {

    @Test
    public void formatting_normal_canBeParsed() throws Exception {
        final LogRecord[] LOG_RECORDS = {
                new LogRecord(Level.SEVERE, "test"),
                new LogRecord(Level.INFO, "fine")
        };

        Handler mockHandler = mock(Handler.class);

        JsonFormatter jsonFormatter = new JsonFormatter();

        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append(jsonFormatter.getHead(mockHandler));
        for (LogRecord record : LOG_RECORDS) {
            logBuilder.append(jsonFormatter.format(record));
        }
        logBuilder.append(jsonFormatter.getTail(mockHandler));

        String formattedLogs = logBuilder.toString();

        JsonArray logs = new JsonParser().parse(formattedLogs).getAsJsonArray();
        assertEquals(LOG_RECORDS.length, logs.size() - 1);

        for (int i = 0; i < LOG_RECORDS.length; i++) {
            JsonObject logRecord = logs.get(i + 1).getAsJsonObject();

            assertEquals(LOG_RECORDS[i].getLevel().toString(), logRecord.get("level").getAsString());
            assertEquals(LOG_RECORDS[i].getMessage(), logRecord.get("message").getAsString());
        }
    }
}