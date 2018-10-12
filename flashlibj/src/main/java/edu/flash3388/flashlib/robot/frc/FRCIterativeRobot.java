package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.robot.scheduling.SchedulerRunMode;
import edu.wpi.first.wpilibj.hal.FRCNetComm;
import edu.wpi.first.wpilibj.hal.HAL;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public abstract class FRCIterativeRobot extends FRCRobotBase {

    private Scheduler mScheduler;

    private boolean mIsDisabledInitialized;
    private boolean mIsTeleopInitialized;
    private boolean mIsAutonomousInitialized;
    private boolean mIsTestInitialized;

    public FRCIterativeRobot() {
        mScheduler = Scheduler.getInstance();
        resetInitializationIndicators();
    }

    @Override
    public void startCompetition() {
        HAL.report(FRCNetComm.tResourceType.kResourceType_Framework, FRCNetComm.tInstances.kFramework_Iterative); // TODO: FIND CONSTANTS FOR THE RESOURCE TYPE

        HAL.observeUserProgramStarting();
        robotInit();

        LiveWindow.setEnabled(false);

        robotLoop();
    }

    private void robotLoop() {
        while (true) {
            m_ds.waitForData();

            if (isDisabled()) {
                if (!mIsDisabledInitialized) {
                    resetInitializationIndicators();
                    resetScheduler(SchedulerRunMode.TASKS_ONLY);
                    LiveWindow.setEnabled(false);

                    disabledInit();

                    mIsDisabledInitialized = true;
                }

                HAL.observeUserProgramDisabled();
                mScheduler.run();
                disabledPeriodic();
            } else if (isOperatorControl()) {
                if (!mIsTeleopInitialized) {
                    resetInitializationIndicators();
                    resetScheduler(SchedulerRunMode.ALL);
                    LiveWindow.setEnabled(true);

                    teleopInit();

                    mIsTeleopInitialized = true;
                }

                HAL.observeUserProgramTeleop();
                mScheduler.run();
                teleopPeriodic();
            } else if (isAutonomous()) {
                if (!mIsAutonomousInitialized) {
                    resetInitializationIndicators();
                    resetScheduler(SchedulerRunMode.ALL);
                    LiveWindow.setEnabled(true);

                    autonomousInit();

                    mIsAutonomousInitialized = true;
                }

                HAL.observeUserProgramAutonomous();
                mScheduler.run();
                autonomousPeriodic();
            } else if (isTest()) {
                if (!mIsTestInitialized) {
                    resetInitializationIndicators();
                    resetScheduler(SchedulerRunMode.ALL);
                    LiveWindow.setEnabled(true);

                    testInit();

                    mIsTestInitialized = true;
                }

                HAL.observeUserProgramTest();
                mScheduler.run();
                testPeriodic();
            }
        }
    }

    private void resetScheduler(SchedulerRunMode runMode) {
        mScheduler.removeAllActions();
        mScheduler.setRunMode(runMode);
    }

    private void resetInitializationIndicators() {
        mIsDisabledInitialized = false;
        mIsTeleopInitialized = false;
        mIsAutonomousInitialized = false;
        mIsTestInitialized = false;
    }

    protected abstract void robotInit();

    protected abstract void disabledInit();
    protected abstract void disabledPeriodic();

    protected abstract void teleopInit();
    protected abstract void teleopPeriodic();

    protected abstract void autonomousInit();
    protected abstract void autonomousPeriodic();

    protected abstract void testInit();
    protected abstract void testPeriodic();
}
