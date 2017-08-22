package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;

public class RemoteSbc implements SBC{

	private Communications communications;
	private RemoteShell shell;
	
	public RemoteSbc(String name, CommInterface inter){
		communications = new Communications(name, inter);
		shell = new RemoteShell();
		communications.attach(shell);
		
		communications.start();
	}
	
	public Shell shell(){
		return shell;
	}
	public Communications communications(){
		return communications;
	}
}
