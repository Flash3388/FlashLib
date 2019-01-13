package edu.flash3388.flashlib.util.logging.jul;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class JsonFormatter extends Formatter {

    private final boolean mShouldFormatMessage;

    private boolean wasFirstLogWritten;

    public JsonFormatter(boolean shouldFormatMessage) {
        mShouldFormatMessage = shouldFormatMessage;
        wasFirstLogWritten = false;
    }

    public JsonFormatter() {
        this(false);
    }

    @Override
    public String getHead(Handler h) {
        wasFirstLogWritten = false;
        return "[";
    }

    @Override
    public String getTail(Handler h) {
        return "]";
    }

    @Override
    public String format(LogRecord record) {
        JsonObject root = new JsonObject();

        addBasicData(root, record);
        addMessage(root, record);
        addParameters(root, record);
        addThrowable(root, record);

        if(wasFirstLogWritten) {
            return ",".concat(root.toString());
        } else {
            wasFirstLogWritten = true;
            return root.toString();
        }
    }

    private void addBasicData(JsonObject root, LogRecord record) {
        root.add("millis", new JsonPrimitive(record.getMillis()));
        root.add("sequence", new JsonPrimitive(record.getSequenceNumber()));
        root.add("level", new JsonPrimitive(record.getLevel().toString()));
        root.add("thread", new JsonPrimitive(record.getThreadID()));

        addIfNotNull(root, "logger", record.getLoggerName());
        addIfNotNull(root, "class", record.getSourceClassName());
        addIfNotNull(root, "method", record.getSourceMethodName());
    }

    private void addIfNotNull(JsonObject root, String name, String data) {
        if (data != null) {
            root.add(name, new JsonPrimitive(data));
        }
    }

    private void addMessage(JsonObject root, LogRecord record) {
        String message = record.getMessage();
        if (message == null) {
            return;
        }

        if (mShouldFormatMessage) {
            message = formatMessage(record);
        }

        root.add("message", new JsonPrimitive(message));
    }

    private void addParameters(JsonObject root, LogRecord record) {
        Object[] parameters = record.getParameters();
        if (parameters == null || parameters.length == 0) {
            return;
        }

        JsonArray parametersJson = new JsonArray();
        for (Object parameter : parameters) {
            parametersJson.add(new JsonPrimitive(parameter.toString()));
        }

        root.add("parameters", parametersJson);
    }

    private void addThrowable(JsonObject root, LogRecord record) {
        Throwable throwable = record.getThrown();
        if (throwable == null) {
            return;
        }

        JsonObject throwableObject = new JsonObject();
        throwableObject.add("message", new JsonPrimitive(throwable.getMessage()));
        throwableObject.add("stacktrace", formatStackTrace(throwable));

        root.add("throwable", throwableObject);
    }

    private JsonArray formatStackTrace(Throwable throwable) {
        JsonArray stacktrace = new JsonArray();

        for (StackTraceElement element : throwable.getStackTrace()) {
            JsonObject elementObject = new JsonObject();
            elementObject.add("native", new JsonPrimitive(element.isNativeMethod()));
            elementObject.add("class", new JsonPrimitive(element.getClassName()));
            elementObject.add("method", new JsonPrimitive(element.getMethodName()));
            elementObject.add("file", new JsonPrimitive(element.getFileName()));
            elementObject.add("line", new JsonPrimitive(element.getLineNumber()));

            stacktrace.add(elementObject);
        }

        return stacktrace;
    }
}
