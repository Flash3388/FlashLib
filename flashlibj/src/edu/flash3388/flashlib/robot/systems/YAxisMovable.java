package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.System;

public interface YAxisMovable {
	void driveY(double speed, boolean direction);
	void forward(double speed);
	void backward(double speed);
	System getSystem();
	void stop();
}
