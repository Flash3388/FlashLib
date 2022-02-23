package com.flash3388.flashlib.scheduling2;

public interface Scheduler {

    Status start(Action action);
    ActionExecutionBuilder submit(Action action);

    //void cancelActionsIf(Predicate<? super Action> predicate); TODO: WHAT PREDICATE
    //void cancelAllActions();
    //void setDefaultAction(Requirement requirement, Action action); // TODO: HOW TO PASS CONFIG

    void run(SchedulerMode mode);
}
