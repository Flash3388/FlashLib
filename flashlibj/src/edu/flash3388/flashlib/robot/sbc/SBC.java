package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;

public interface SBC {

	Shell getShell();
	Communications getCommunications();
	
	
	public static interface SbcData{
		CommInterface createCommInterface();
		String getName();
	}
	
	public static SBC createRemote(SbcData data){
		CommInterface comm = data.createCommInterface();
		if(comm == null)
			return null;
		return new RemoteSBC(data.getName(), comm);
	}
}
