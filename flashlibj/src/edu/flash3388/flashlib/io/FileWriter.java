package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.IOException;


/**
 * Allows to write info to a file on the robot.
 * 
 * @author Tom Tzook
 */
public class FileWriter {
	private static final String PARENT_DIRECTORY = "";//"data/";
	private File file;
	private java.io.FileWriter writer;
	private String uri;
	private boolean is_closed = false;
	
	/**
	 * Creates a new file at a desired uri. If a file exists than it is deleted.
	 * A writer is created to write info to the file.
	 * 
	 * @param uri The location of the file.
	 * @throws NullPointerException if uri is null
	 */
	public FileWriter(String uri) throws NullPointerException{
		if(!uri.startsWith(PARENT_DIRECTORY))
			this.uri = PARENT_DIRECTORY + uri;
		else this.uri = uri;
		
		try {
			file = new File(this.uri);
			if(!file.exists()){
				String par = file.getParent();
				if(par != null)
					new File(par).mkdirs();
				file.createNewFile();
			}
			
			writer = new java.io.FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes info to the log file if the log file exists and can be written to. If the writer was closed then
	 * nothing will happen. The info cannot be null or empty.
	 * 
	 * @param info A string of information to write to the file. Cannot be null or empty.
	 */
	public void write(String info){
		if(file.exists() && info != null && info != "" && !is_closed && file.canWrite()){
			try {
				writer.write(info + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void write(String...lines){
		for (String line : lines)
			write(line);
	}
	
	public void writeConstant(String constant, String value, String seperator){
		write(constant+seperator+value);
	}
	public void writeConstant(String constant, String value){
		writeConstant(constant, value, FileReader.DEFAULT_CONSTANT_SEPERATOR);
	}
	
	/**
	 * returns if the writer is closed or not.
	 * @return True if the writer is closed, false otherwise.
	 */
	public boolean isClosed(){
		return is_closed;
	}
	
	public void delete(){
		close();
		file.delete();
	}
	
	/**
	 * Closes the writer of the file. Closing will disable writing to the file.
	 */
	public void close(){
		if(is_closed || writer == null) return;
		try {
			writer.flush();
			writer.close();
			is_closed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
