package edu.flash3388.flashlib.robot;

import java.io.IOException;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;

public class RemoteSBC implements SBC{

	private Communications communications;
	private RemoteShell shell;
	
	public RemoteSBC(String name, CommInterface inter) throws SecurityException, IOException{
		communications = new Communications(name, inter);
		shell = new RemoteShell();
		communications.attach(shell);
		
		communications.start();
	}
	
	public Shell getShell(){
		return shell;
	}
	public Communications getCommunications(){
		return communications;
	}
}
