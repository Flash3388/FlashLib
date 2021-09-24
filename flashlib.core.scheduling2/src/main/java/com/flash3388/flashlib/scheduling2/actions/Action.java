package com.flash3388.flashlib.scheduling2.actions;

public interface Action {

    void configure(Configuration configuration);

    void initialize(Control control);
    void execute(Control control);
    void end(Control control);
}
