package edu.flash3388.flashlib.io;

import java.io.File;
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
}
