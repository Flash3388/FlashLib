package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.robot.FlashRobotUtil;

/**
 * Represents emergency stop control on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class EmergencyStopControl extends Sendable{

	private static final byte EMERGENCY = 0xe;
	private static final byte NORMAL = 0x5;
	
	private boolean changed = false;
	private boolean emergency = false;
	
	public EmergencyStopControl() {
		super("Emergency Stop", FlashboardSendableType.ESTOP);
	}

	public void inEmergencyStop(boolean e){
		if(e != emergency){
			emergency = e;
			changed = true;
		}
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 1)return;
		
		if(data[0] == EMERGENCY){
			FlashRobotUtil.enterEmergencyStop();
			emergency = true;
		}
		else if(data[0] == NORMAL){
			FlashRobotUtil.exitEmergencyStop();
			emergency = false;
		}
		
		changed = true;
	} 
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		return new byte[]{emergency? EMERGENCY : NORMAL};
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void onConnection() {
		emergency = FlashRobotUtil.inEmergencyStop();
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
