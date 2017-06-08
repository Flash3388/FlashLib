package edu.flash3388.flashlib.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileStream {
	private FileStream(){}
	
	public static String[] filesInFolder(String path, String extension){
		File[] files = new File(path).listFiles();
		if(files == null)
			return null;
		
		List<String> names = new ArrayList<String>();
		for(File f : files){
			if(f.isFile()){
				String[] splits = f.getPath().split("\\.");
				if((extension.equals("")) || 
						(splits.length > 1 && splits[splits.length-1].equals(extension)))	
					names.add(f.getName());
			}
		}
		String[] namesA = new String[names.size()];
		return names.toArray(namesA);
	}
	public static String[] filesInFolder(String directory){
		return filesInFolder(directory, "");
	}
	
	public static String fileName(String file){
		return new File(file).getName();
	}
	
	public static FileWriter openWriter(String file){
		return new FileWriter(file);
	}
	public static FileReader openReader(String file) throws NullPointerException, FileNotFoundException{
		return new FileReader(file);
	}
	public static File wrap(String filename){
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
	
	public static String[] readAllLines(String file) throws NullPointerException, IOException{
		FileReader reader = new FileReader(file);
		String[] lines = reader.readAll();
		reader.close();
		return lines;
	}
	public static void writeLines(String file, String[] lines){
		FileWriter writer = new FileWriter(file);
		writer.write(lines);
		writer.close();
	}
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
	public static void writeLine(String file, String line){
		FileWriter writer = new FileWriter(file);
		writer.write(line);
		writer.close();
	}
}
