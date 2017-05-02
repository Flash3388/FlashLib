package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.System;

public interface Rotatable {
	void rotate(double speed, int direction);
	void rotateRight(double speed);
	void rotateLeft(double speed);
	void stop();
	System getSystem();
}
