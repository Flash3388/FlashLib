package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides utilities for handling files.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FileStream {
	private FileStream(){}
	
	/**
	 * Gets an array of names of all the files with a specific extension in a directory.
	 * If the extension is an empty string, all files will be added to the array.
	 * 
	 * @param path the path of the directory
	 * @param extension the extension of the wanted files
	 * @return an array of names of all the files with a wanted extension in a directory 
	 * @see File#listFiles()
	 */
	public static String[] filesInFolder(String path, String extension){
		File[] files = new File(path).listFiles();
		if(files == null)
			return null;
		
		List<String> names = new ArrayList<String>();
		for(File f : files){
			if(f.isFile()){
				String[] splits = f.getPath().split("\\.");
				if((extension.length() == 0) || 
						(splits.length > 1 && splits[splits.length-1].equals(extension)))	
					names.add(f.getName());
			}
		}
		String[] namesA = new String[names.size()];
		return names.toArray(namesA);
	}
	/**
	 * Gets an array of names of all the files in a directory.
	 * 
	 * @param directory the path of the directory
	 * @return an array of names of all the files in a directory 
	 * @see File#listFiles()
	 */
	public static String[] filesInFolder(String directory){
		return filesInFolder(directory, "");
	}
	
	/**
	 * Gets the name of the file.
	 * 
	 * @param file the file path
	 * @return the name of the file
	 */
	public static String fileName(String file){
		return new File(file).getName();
	}
	
	/**
	 * Opens a {@link FileWriter} to a file path and returns the object
	 * 
	 * @param file the file path
	 * @return a {@link FileWriter} object to a file path
	 */
	public static FileWriter openWriter(String file){
		return new FileWriter(file);
	}
	/**
	 * Opens a {@link FileReader} to a file path and returns the object
	 * 
	 * @param file the file path
	 * @return a {@link FileReader} object to a file path
	 * @throws FileNotFoundException if the file was not found
	 * @throws NullPointerException if the file path is null
	 */
	public static FileReader openReader(String file) throws NullPointerException, FileNotFoundException{
		return new FileReader(file);
	}
	
	/**
	 * Gets a {@link File} object for a file path. If any directories in the file
	 * path do not exist, they are created. If the file does not exist, it is created.
	 * 
	 * @param filename the file path
	 * @return a {@link File} object for a file path
	 */
	public static File getFile(String filename){
        File file = new File(filename);
        
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) 
            parent.mkdirs();
        
        if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {}
        }
        
        return file;
	}
	
	/**
	 * Reads all lines in a file and returns them. Opens a {@link FileReader} and 
	 * calls {@link FileReader#readAll()}.
	 * 
	 * @param file the file path
	 * @return all the lines in the file
	 * @throws NullPointerException if the file path is null
	 * @throws IOException if an IO exception has occurred
	 * @see FileReader#readAll()
	 */
	public static String[] readAllLines(String file) throws NullPointerException, IOException{
		FileReader reader = new FileReader(file);
		String[] lines = reader.readAll();
		reader.close();
		return lines;
	}
	/**
	 * Writes lines to a FileWriter. Opens a {@link FileWriter} and 
	 * calls {@link FileWriter#write(String...)}.
	 * 
	 * @param file the file path
	 * @param lines lines to write to the file
	 * @see FileWriter#write(String...)
	 */
	public static void writeLines(String file, String[] lines){
		FileWriter writer = new FileWriter(file);
		writer.write(lines);
		writer.close();
	}
	/**
	 * Adds lines to a file. Opens a {@link FileReader}, reads all lines by 
	 * calling {@link FileReader#readAll()}, than writes back all the read lines and new lines.
	 * 
	 * @param file the file path
	 * @param lines lines to write to the file
	 * @see #readAllLines(String)
	 * @see FileWriter#write(String...)
	 */
	public static void appendLines(String file, String[] lines){
		String[] readLines = null;
		try {
			readLines = readAllLines(file);
		} catch (NullPointerException | IOException e) {
		}
		FileWriter writer = new FileWriter(file);
		if(readLines != null)
			writer.write(readLines);
		writer.write(lines);
		writer.close();
	}
	/**
	 * Writes a line to a FileWriter. Opens a {@link FileWriter} and 
	 * calls {@link FileWriter#write(String)}.
	 * 
	 * @param file the file path
	 * @param line line to write to the file
	 * @see FileWriter#write(String)
	 */
	public static void writeLine(String file, String line){
		FileWriter writer = new FileWriter(file);
		writer.write(line);
		writer.close();
	}
}
