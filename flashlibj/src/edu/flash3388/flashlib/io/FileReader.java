package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A file reader for reading file data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FileReader {
	public static final String DEFAULT_CONSTANT_SEPERATOR = ":";

	private static String PARENT_DIRECTORY = "";//"data/";
	
	private File file;
	private java.io.FileReader reader;
	private String uri;
	private boolean is_closed = true;
	private boolean endOfStream = false;
	
	private List<String> lines = new ArrayList<String>();
	private String currentLine = "";
	
	/**
	 * Opens a file at the give URI for reading.
	 * 
	 * @param uri The location of the file.
	 * @throws NullPointerException If given URI is null
	 * @throws FileNotFoundException If file was now found
	 */
	public FileReader(String uri) throws NullPointerException, FileNotFoundException{
		if(!uri.startsWith(PARENT_DIRECTORY))
			this.uri = PARENT_DIRECTORY + uri;
		else this.uri = uri;
		
		file = new File(this.uri);
		if(!file.exists()) 
			throw new java.io.FileNotFoundException("File was not found");
		
		try {
			reader = new java.io.FileReader(file);
			is_closed = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets a constant from the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * The file is searched from the start line to the end line.
	 * 
	 * @param constant the constant identifier
	 * @param seperator the separator string between the identifier and the value
	 * @param fromLine the starting line to read from
	 * @param toLine the end line for reading
	 * @return the value of the identifier, or null if it was not found.
	 * 
	 * @throws IOException if an I/O error has occurred
	 * @throws IllegalArgumentException if the line parameters are invalid
	 */
	public String getConstant(String constant, String seperator, int fromLine, int toLine) throws IOException{
		if(fromLine < 0 || (fromLine > toLine && toLine > 0))
			throw new IllegalArgumentException("Invalid line parameters");
		
		int cLine = fromLine;
		while(((toLine > 0 && cLine < toLine) && cLine > fromLine) || (toLine < 0 && !endOfStream)){
			
			String line = cLine >= lines.size() ? readLine() : lines.get(cLine);
			
			if(endOfStream || line == null)
				break;
			
			String[] splits = line.split(seperator);
			if(splits.length < 2) {
				cLine++;
				continue;
			}
			
			if(splits[0].equals(constant)){
				String returnS = splits[1];
				if(splits.length > 2){
					for(int j = 2; j < splits.length; j++)
						returnS += splits[j];
				}
				return returnS;
			}
			cLine++;
		}
		
		return null;
	}
	/**
	 * Gets a constant from the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * The file is searched from the start line to the end.
	 * 
	 * @param constant the constant identifier
	 * @param seperator the separator string between the identifier and the value
	 * @param fromLine the starting line to read from
	 * @return the value of the identifier, or null if it was not found.
	 * 
	 * @throws IOException if an I/O error has occurred
	 * @throws IllegalArgumentException if the line parameters are invalid
	 */
	public String getConstant(String constant, String seperator, int fromLine) throws IOException{
		return getConstant(constant, seperator, fromLine, -1);
	}
	/**
	 * Gets a constant from the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * The file is searched from the start to the end.
	 * 
	 * @param constant the constant identifier
	 * @param seperator the separator string between the identifier and the value
	 * @return the value of the identifier, or null if it was not found.
	 * 
	 * @throws IOException if an I/O error has occurred
	 * @throws IllegalArgumentException if the line parameters are invalid
	 */
	public String getConstant(String constant, String seperator) throws IOException{
		return getConstant(constant, seperator, 0, -1);
	}
	/**
	 * Gets a constant from the file. A constant is a represented in the following manner:<br>
	 * {@code
	 * 	constant:value
	 * }<br>
	 * Where : is the separator.<br>
	 * The file is searched from the start line to the end.
	 * 
	 * @param constant the constant identifier
	 * @return the value of the identifier, or null if it was not found.
	 * 
	 * @throws IOException if an I/O error has occurred
	 * @throws IllegalArgumentException if the line parameters are invalid
	 */
	public String getConstant(String constant) throws IOException{
		return getConstant(constant, DEFAULT_CONSTANT_SEPERATOR, 0, -1);
	}
	
	/**
	 * Reads all lines in the file and returns them.
	 * 
	 * @return all the files in the file
	 * @throws IOException if an I/O error occurs
	 */
	public String[] readAll() throws IOException{
		while(!endOfStream)
			readLine();
		return lines.toArray(new String[lines.size()]);
	}
	/**
	 * Reads a line from the file, until '\n'.
	 * 
	 * @return A line of String from the file.
	 * @throws IOException If an I/O error occurs
	 * @see #readUntil(char)
	 */
	public String readLine() throws IOException{
		return readUntil('\n');
	}
	
	/**
	 * Reads all characters and a specific character is reached.
	 * 
	 * @param ch the ending character
	 * @return a string of all characters up until the end character
	 * @throws IOException if an I/O error occurs
	 */
	public String readUntil(char ch) throws IOException{
		if(endOfStream)
			return null;
		
		String str = "";
		char c = read();
		
		if(c == '\u0000')
			return null;
		
		while(c != ch && c != '\u0000'){
			str += c;
			c = read();
		}
		
		return str;
	}
	
	/**
	 * Reads a single character from the file. Returns a null character if 
	 * reached the end of stream. Read values are saved in a collection.
	 * 
	 * @return A character from the file.
	 * @throws IOException If an I/O error occurs
	 */
	public char read() throws IOException{
		if(endOfStream)
			return '\u0000';
		
		int n = reader.read();
		
		if(n == -1){
			endOfStream = true;
			lines.add(currentLine);
			currentLine = "";
			return '\u0000';
		}
		
		char c = (char)n;
		if(c == '\n'){
			lines.add(currentLine);
			currentLine = "";
		}else currentLine += c;
		
		return c;
	}
	
	/**
	 * Gets a line read by this instance. If the index is bigger than the amount of files
	 * read, null is returned.
	 * 
	 * @param index the index 
	 * @return the line at that index
	 */
	public String getReadLine(int index){
		if(index >= lines.size())
			return null;
		return lines.get(index);
	}
	
	/**
	 * Returns whether the reader is closed or not.
	 * @return True if the reader is closed, false otherwise.
	 */
	public boolean isClosed(){
		return is_closed;
	}
	
	/**
	 * Closes the reader and deletes the file.
	 */
	public void delete(){
		close();
		file.delete();
	}
	
	/**
	 * Closes the reader of the file. Closing will disable reading to the file.
	 */
	public void close(){
		if(is_closed) return;
		try {
			//readAll();
			reader.close();
			is_closed = true;
			//restoreFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the directory in which to search files whose directories were not defined.
	 * 
	 * @param directory The directory in which to search for files.
	 */
	public static void setParentDirectory(String directory){
		PARENT_DIRECTORY = directory;
		if(!directory.isEmpty() && !PARENT_DIRECTORY.endsWith("/"))
			PARENT_DIRECTORY += "/";
	}
}
