package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;

public class RemoteShell extends Sendable implements Shell{
	
	private static class RemoteShellOutputStream extends OutputStream{
		private Vector<Byte> bytes = new Vector<Byte>();

		@Override
		public void write(int b) throws IOException {
			bytes.add((byte)b);
		}
	}
	private static class RemoteShellInputStream extends InputStream{

		private Vector<Byte> bytes = new Vector<Byte>();
		
		@Override
		public int read() throws IOException {
			synchronized (bytes) {
				if(bytes.size() < 1)
					return -1;
				return bytes.remove(0);
			}
		}
		@Override
		public int available() throws IOException {
			return bytes.size();
		}
	}

	private byte exectutionCode = EXECUTE_IDLE;
	
	private boolean connected = false, updateOutputStream = false, stopProcess = false, startProcess = false;
	private String command = null;
	
	private RemoteShellInputStream in = new RemoteShellInputStream();
	private RemoteShellOutputStream out = new RemoteShellOutputStream();
	
	public RemoteShell() {
		super("", SbcSendableType.SHELL_EXECUTOR);
	}
	
	@Override
	public boolean execute(String command) {
		if(!connected || isExecuting())
			return false;
		this.command = command;
		startProcess = true;
		return true;
	}
	@Override
	public boolean isExecuting() {
		return exectutionCode == EXECUTE_PROGRESS;
	}
	@Override
	public int getExecutionCode() {
		return exectutionCode;
	}
	@Override
	public void stopExecution() {
		if(isExecuting())
			stopProcess = true;
	}

	@Override
	public OutputStream getOutputStream() {
		if(!isExecuting())
			return null;
		return out;
	}
	@Override
	public InputStream getInputStream() {
		if(!isExecuting())
			return null;
		return in;
	}
	

	@Override
	public void newData(byte[] data) {
		if(data[0] == EXECUTE_CODE_UPDATE){
			data[1] = data[1];
			in.bytes.clear();
			out.bytes.clear();
		}
		else if(data[0] == INPUT_STREAM_UPDATE){
			for (int i = 1; i < data.length; i++)
				in.bytes.add(data[i]);
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(stopProcess){
			stopProcess = false;
			return new byte[]{EXECUTE_STOP};
		}
		if(startProcess){
			startProcess = false;
			byte[] data = new byte[command.length() + 1];
			data[0] = EXECUTE_START;
			System.arraycopy(command.getBytes(), 0, data, 1, command.length());
			command = null;
			return data;
		}
		if(updateOutputStream){
			updateOutputStream = false;
			Enumeration<Byte> bytes = out.bytes.elements();
			byte[] data = new byte[out.bytes.size()];
			int c = 0;
			while(bytes.hasMoreElements())
				data[c++] = bytes.nextElement();
			return data;
		}
		return null;
	}
	@Override
	public boolean hasChanged() {
		return startProcess || stopProcess || updateOutputStream;
	}

	@Override
	public void onConnection() {
		connected = true;
	}
	@Override
	public void onConnectionLost() {
		connected = false;
		startProcess = false;
		stopProcess = false;
		updateOutputStream = false;
		exectutionCode = EXECUTE_IDLE;
	}
}
