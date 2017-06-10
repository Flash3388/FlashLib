package edu.flash3388.flashlib.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

public class CommandParser{

	public static interface Command{
		String getName();
		String getDescription();
		void exec(String[] args, Scanner in, PrintStream out);
	}
	
	private boolean stop = false;
	private String name;
	private final Scanner in;
	private final PrintStream out;
	private HashMap<String, Command> commands = new HashMap<String, Command>();
	
	public CommandParser(String name){
		this(name, new Scanner(System.in), System.out);
	}
	public CommandParser(String name, InputStream in, OutputStream out){
		this(name, new Scanner(in), new PrintStream(out));
	}
	public CommandParser(String name, Scanner scI, PrintStream p){
		this.in = scI;
		this.out = p;
		this.name = name;
		loadDefaultCommands();
	}
	
	private void loadDefaultCommands(){
		addCommand(new Command(){
			@Override
			public String getName() {
				return "help";
			}
			@Override
			public String getDescription() {
				return "Prints all available commands";
			}
			@Override
			public void exec(String[] args, Scanner in, PrintStream out) {
				printCommands();
			}
		});
		addCommand(new Command(){
			@Override
			public String getName() {
				return "exit";
			}
			@Override
			public String getDescription() {
				return "Closes the command parser";
			}
			@Override
			public void exec(String[] args, Scanner in, PrintStream out) {
				stop();
			}
		});
	}
	private void printCommands(){
		String str = "";
		for (Command command : commands.values()) 
			str += "\t"+command.getName()+" - "+command.getDescription()+"\n";
		out.println(str.substring(0, str.length()-2));
	}
	
	public boolean addCommand(Command command){
		if(getCommand(command.getName()) != null)
			return false;
		
		commands.put(command.getName(), command);
		return true;
	}
	public void addCommands(Command...commands){
		for (Command command : commands) 
			addCommand(command);
	}
	public boolean removeCommand(String name){
		return commands.remove(name) != null;
	}
	public boolean removeCommand(Command command){
		return removeCommand(command.getName());
	}
	public Command getCommand(String name){
		return commands.get(name);
	}
	public Command[] getCommands(){
		Command[] commandsArr = new Command[commands.size()];
		return commands.values().toArray(commandsArr);
	}
	
	public Scanner getInputScanner(){
		return in;
	}
	public PrintStream getPrintStream(){
		return out;
	}
	
	public String readLine(){
		out.print(name+">");
		return in.nextLine();
	}
	public void print(String str){
		out.print(str);
	}
	public void println(String ln){
		out.println(ln);
	}
	
	public void parse(String line){
		if(line.length() < 1){
			out.println("Parse Error: Empty command");
			return;
		}
		
		int index = line.indexOf(' ');
		if(index < 0) index = line.length();
		
		String comm = line.substring(0, index);
		String[] args = index + 1 >= line.length()? new String[0] : 
			line.substring(index + 1).split(" ");
		
		Command command = getCommand(comm);
		if(command != null)
			command.exec(args, in, out);
		else 
			out.println("Parse Error: Unknown command "+comm);
	}
	public void run(){
		stop = false;
		String line = "";
		while (!stop) {
			line = readLine();
			parse(line);
		}
	}
	public void stop(){
		stop = true;
	}
}
