package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows to read files from the robot.
 * 
 * @author Tom Tzook
 */
public class FileReader {
	public static final String DEFAULT_CONSTANT_SEPERATOR = ":";
	private static final String PARENT_DIRECTORY = "";//"data/";
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
	 * @throws NullPointerException If there is no file at the give URI.
	 * @throws FileNotFoundException 
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
	public String getConstant(String constant, String seperator, int fromLine) throws IOException{
		return getConstant(constant, seperator, fromLine, -1);
	}
	public String getConstant(String constant, String seperator) throws IOException{
		return getConstant(constant, seperator, 0, -1);
	}
	public String getConstant(String constant) throws IOException{
		return getConstant(constant, DEFAULT_CONSTANT_SEPERATOR, 0, -1);
	}
	
	public String[] readAll() throws IOException{
		while(!endOfStream)
			readLine();
		return lines.toArray(new String[0]);
	}
	/**
	 * Reads a line from the file, until '\n'.
	 * 
	 * @return A line of String from the file.
	 * @throws IOException If an I/O error occurs
	 */
	public String readLine() throws IOException{
		return readUntil('\n');
	}
	
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
	 * Reads a single character from the file.
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
	
	public String getReadLine(int index){
		if(index > lines.size())
			return null;
		return lines.get(index);
	}
	
	/**
	 * returns if the reader is closed or not.
	 * @return True if the reader is closed, false otherwise.
	 */
	public boolean isClosed(){
		return is_closed;
	}
	
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
}
