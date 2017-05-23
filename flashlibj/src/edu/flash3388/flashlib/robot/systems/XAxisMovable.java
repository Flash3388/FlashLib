package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.System;

public interface XAxisMovable {
	void driveX(double speed, boolean direction);
	void right(double speed);
	void left(double speed);
	void stop();
	System getSystem();
}
