package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.IOException;


/**
 * A file writer for writing file data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FileWriter {
	
	private static String PARENT_DIRECTORY = "";//"data/";
	
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
	 * Writes a line to the file if the file exists and can be written to. If the writer was closed then
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
	/**
	 * Writes data to the file if the file exists and can be written to. If the writer was closed then
	 * nothing will happen. The info cannot be null or empty.
	 * 
	 * @param info A string of information to write to the file. Cannot be null or empty.
	 */
	public void write(String...lines){
		for (int i = 0; i < lines.length; i++)
			write(lines[i]);
	}
	
	/**
	 * Writes a constant to the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * 
	 * @param constant the constant identifier
	 * @param seperator the separator string between the identifier and the value
	 * @param value the value attached to the identifier
	 */
	public void writeConstant(String constant, String value, String seperator){
		write(constant+seperator+value);
	}
	/**
	 * Writes a constant to the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * 
	 * @param constant the constant identifier
	 * @param value the value attached to the identifier
	 */
	public void writeConstant(String constant, String value){
		writeConstant(constant, value, FileReader.DEFAULT_CONSTANT_SEPERATOR);
	}
	
	/**
	 * Returns whether the writer is closed or not.
	 * @return True if the writer is closed, false otherwise.
	 */
	public boolean isClosed(){
		return is_closed;
	}
	
	/**
	 * Closes the writer and deletes the file.
	 */
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
	
	/**
	 * Sets the directory in which to save files whose directories were not defined.
	 * 
	 * @param directory The directory in which to save for files.
	 */
	public static void setParentDirectory(String directory){
		PARENT_DIRECTORY = directory;
		if(!directory.isEmpty() && !PARENT_DIRECTORY.endsWith("/"))
			PARENT_DIRECTORY += "/";
	}
}
