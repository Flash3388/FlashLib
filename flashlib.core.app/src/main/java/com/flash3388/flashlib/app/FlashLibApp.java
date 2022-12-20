package com.flash3388.flashlib.app;

public interface FlashLibApp {

    void initialize(FlashLibControl control) throws StartupException;
    void main(FlashLibControl control) throws Exception;
    void shutdown(FlashLibControl control) throws Exception;
}
