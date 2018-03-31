package edu.flash3388.flashlib.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

public class LocalShell extends Sendable implements Shell{

	public static class ProcessTracker implements Runnable{

		private boolean stop = false;
		private boolean updateData = false;
		
		private LocalShell shell;
		
		@Override
		public void run() {
			while(!stop){
				if(updateData){
					if(shell.stopExecution){
						shell.stopExecution = false;
						shell.stopExecution();
					}
					if(shell.newCommandReceived){
						shell.newCommandReceived = false;
						shell.execute(shell.newCommand);
						shell.newCommand = null;
					}
					
					if(shell.currentProcess != null){
						if(shell.currentProcess.isAlive() && shell.connected){
							InputStream in = shell.currentProcess.getInputStream();
							
							try {
								if(!shell.updateInputStream && in.available() > 0){
									byte[] data = new byte[in.available()];
									int read = in.read(data);
									if(read > 0){
										shell.inputStreamData = data;
										shell.updateInputStream = true;
									}
								}
							} catch (IOException e) {
							}
							
							if(shell.outputStreamUpdated){
								shell.outputStreamUpdated = false;
								
								byte[] outputData = shell.outputStreamData;
								try {
									shell.currentProcess.getOutputStream().write(outputData);
								} catch (IOException e) {
								}
							}
						}
						if(shell.currentProcess.isAlive() && shell.executionCode != EXECUTE_PROGRESS){
							shell.executionCode = EXECUTE_PROGRESS;
							shell.updateExecutioncode = true;
						}else if(!shell.currentProcess.isAlive() && shell.executionCode != EXECUTE_IDLE){
							shell.executionCode = EXECUTE_IDLE;
							shell.updateExecutioncode = true;
							shell.currentProcess = null;
						}
					}
				}
				FlashUtil.delay(10);
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private byte executionCode;
	private Process currentProcess;
	private ProcessTracker tracker;
	
	private boolean updateExecutioncode = false,
			updateInputStream = false;
	private boolean outputStreamUpdated = false, newCommandReceived = false,
			stopExecution = false;
	private boolean connected = false;
	
	private String newCommand = null;
	private byte[] outputStreamData = null;
	private byte[] inputStreamData = null;
	
	public LocalShell() {
		super("", SENDABLE_TYPE);
		tracker = new ProcessTracker();
		tracker.shell = this;
	}
	
	public ProcessTracker getTracker(){
		return tracker;
	}
	
	@Override
	public boolean execute(String command) {
		if(isExecuting())
			return false;
		try {
			currentProcess = Runtime.getRuntime().exec(command);
			executionCode = EXECUTE_PROGRESS;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public boolean isExecuting() {
		return executionCode == EXECUTE_PROGRESS;
	}
	@Override
	public int getExecutionCode() {
		return executionCode;
	}
	@Override
	public void stopExecution() {
		if(currentProcess != null)
			currentProcess.destroy();
	}

	@Override
	public OutputStream getOutputStream() {
		if(currentProcess != null)
			return currentProcess.getOutputStream();
		return null;
	}
	@Override
	public InputStream getInputStream() {
		if(currentProcess != null)
			return currentProcess.getInputStream();
		return null;
	}

	
	@Override
	public void newData(byte[] data) {
		tracker.updateData = false;
		if(data[0] == EXECUTE_START){
			String command = new String(data, 1, data.length - 1);
			
			newCommand = command;
			newCommandReceived = true;
		}else if(data[0] == EXECUTE_STOP){
			stopExecution = true;
		}
		else if(data[0] == OUTPUT_STREAM_UPDATE){
			outputStreamData = Arrays.copyOfRange(data, 1, outputStreamData.length - 1);
			outputStreamUpdated = true;
		}
		tracker.updateData = true;
	}

	@Override
	public byte[] dataForTransmission() {
		if(updateInputStream){
			updateInputStream = false;
			byte[] data = new byte[inputStreamData.length + 1];
			System.arraycopy(inputStreamData, 0, data, 1, inputStreamData.length);
			data[0] = INPUT_STREAM_UPDATE;
			return data;
		}
		if(updateExecutioncode){
			updateExecutioncode = false;
			byte[] data = new byte[2];
			data[0] = EXECUTE_CODE_UPDATE;
			data[1] = executionCode;
		}
		return null;
	}

	@Override
	public boolean hasChanged() {
		return updateInputStream || updateExecutioncode;
	}

	@Override
	public void onConnection() {
		connected = true;
		updateExecutioncode = true;
	}
	@Override
	public void onConnectionLost() {
		connected = false;
	}
}
