package edu.flash3388.flashlib.robot.frc;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import edu.wpi.first.wpilibj.DriverStation;

public class DriverStationLogHandler extends Handler {
	
	@Override
	public void publish(LogRecord record) {
		String log = String.format("[%s] (%s:%s): %s", 
				record.getMillis(),
				record.getSourceClassName(), record.getSourceMethodName(),
				record.getMessage());
		
		if (record.getLevel() == Level.WARNING) {
			DriverStation.reportWarning(log, false);
		}
		else if (record.getLevel() == Level.SEVERE) {
			DriverStation.reportError(log, false);
		}
		else {
			System.out.println(String.format("%s> <%s> %s", 
					record.getLoggerName(), record.getLevel(), log));
		}
	}
	
	@Override
	public void flush() {
	}
	@Override
	public void close() throws SecurityException {
	}
}
