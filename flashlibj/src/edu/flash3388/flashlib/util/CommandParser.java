package edu.flash3388.flashlib.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * CommandParser provides a shell for custom commands. Each command is represented in the {@link Command} interface and saved
 * in an {@link java.util.HashMap}. For IO, {@link java.io.InputStream} and {@link java.io.OutputStream} can be set.
 * 
 * <p>
 * Calling the {@link #run()} will start a loop of command execution, in which commands will be read and executed.
 * </p>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CommandParser{

	/**
	 * An interface representing a command for the {@link CommandParser}.
	 * To execute the command, calling its name from the parser's input stream is needed.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static interface Command{
		/**
		 * Gets the name of the command.
		 * @return the name of the command
		 */
		String getName();
		/**
		 * Gets the description of the command, explaining what the command is meant to do. This is used 
		 * when an "help" command is called.
		 * 
		 * @return the description of the command
		 */
		String getDescription();
		/**
		 * Executes the command. A {@link java.util.Scanner} and {@link java.io.PrintStream} are passed to allow for user 
		 * interaction. A string array of arguments passed from the user are also passed.
		 * 
		 * @param args arguments from the user
		 * @param in a {@link java.util.Scanner} to use for input from the user
		 * @param out a {@link java.io.PrintStream} to use for output to the user
		 */
		void exec(String[] args, Scanner in, PrintStream out);
	}
	
	private boolean stop = false;
	private String name;
	private final Scanner in;
	private final PrintStream out;
	private HashMap<String, Command> commands = new HashMap<String, Command>();
	
	/**
	 * Creates a new instance of CommandParser which uses a given {@link java.lang.System#in} for user input and 
	 * {@link java.lang.System#out} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all commands</li>
	 * 		<li>exit: closes the parser's execution loop</li>
	 * </ul>
	 * @param name the name of the parser
	 */
	public CommandParser(String name){
		this(name, new Scanner(System.in), System.out);
	}
	/**
	 * Creates a new instance of CommandParser which uses a given {@link java.io.InputStream} for user input and 
	 * {@link java.io.OutputStream} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all commands</li>
	 * 		<li>exit: closes the parser's execution loop</li>
	 * </ul>
	 * @param name the name of the parser
	 * @param in input stream from the user
	 * @param out output stream to the user
	 */
	public CommandParser(String name, InputStream in, OutputStream out){
		this(name, new Scanner(in), new PrintStream(out));
	}
	/**
	 * Creates a new instance of CommandParser which uses a given {@link java.util.Scanner} for user input and 
	 * {@link java.io.PrintStream} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all commands</li>
	 * 		<li>exit: closes the parser's execution loop</li>
	 * </ul>
	 * @param name the name of the parser
	 * @param scI an input stream wrapped as {@link java.util.Scanner}
	 * @param p an output stream wrapped as {@link java.io.PrintStream}
	 */
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
	
	/**
	 * Add a new command to the parser. If a command with the same name already exists, the command will not be added.
	 * 
	 * @param command the {@link Command}
	 * @return true if the command was added, false otherwise
	 */
	public boolean addCommand(Command command){
		if(getCommand(command.getName()) != null)
			return false;
		
		commands.put(command.getName(), command);
		return true;
	}
	/**
	 * Add new commands to the parser. If a command with the same name already exists, the command will not be added.
	 * 
	 * @param commands array of {@link Command}
	 */
	public void addCommands(Command...commands){
		for (Command command : commands) 
			addCommand(command);
	}
	/**
	 * Removes a command from the command map if it exists. 
	 * 
	 * @param name the name of the command to remove
	 * @return true if the command was remove, false otherwise
	 */
	public boolean removeCommand(String name){
		return commands.remove(name) != null;
	}
	/**
	 * Removes a command from the command map if it exists. 
	 * 
	 * @param command the {@link Command} to remove
	 * @return true if the command was remove, false otherwise
	 */
	public boolean removeCommand(Command command){
		return removeCommand(command.getName());
	}
	/**
	 * Returns a command from the name if it exists.
	 * 
	 * @param name the name of the {@link Command}
	 * @return the {@link Command} if it exists, null otherwise
	 */
	public Command getCommand(String name){
		return commands.get(name);
	}
	/**
	 * Returns an array of all the commands in the map.
	 * 
	 * @return an array of {@link Command}s in the parser map
	 */
	public Command[] getCommands(){
		Command[] commandsArr = new Command[commands.size()];
		return commands.values().toArray(commandsArr);
	}
	
	/**
	 * Returns the {@link java.util.Scanner} object used for user input.
	 * 
	 * @return the input {@link java.util.Scanner} object 
	 */
	public Scanner getInputScanner(){
		return in;
	}
	/**
	 * Returns the {@link java.io.PrintStream} object used for user output.
	 * 
	 * @return the output {@link java.io.PrintStream} object 
	 */
	public PrintStream getPrintStream(){
		return out;
	}
	
	/**
	 * Reads a line from the {@link java.util.Scanner} and returns.
	 * 
	 * @return a line from the {@link java.util.Scanner} object
	 */
	public String readLine(){
		out.print(name+">");
		return in.nextLine();
	}
	/**
	 * Prints a string into the {@link java.io.PrintStream}.
	 * 
	 * @param str the string to print
	 */
	public void print(String str){
		out.print(str);
	}
	/**
	 * Prints a string into the {@link java.io.PrintStream} and terminates the line.
	 * 
	 * @param ln the string to print
	 */
	public void println(String ln){
		out.println(ln);
	}
	
	/**
	 * Parses a string and execute any given command if found. <br>The correct syntax for commands is:
	 * {@code
	 * 		command_name arg1 arg2 arg3....
	 * }<br>
	 * If an error occurs while parsing the line, it is printed into the output stream.
	 * 
	 * @param line the string to parse.
	 */
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
	/**
	 * Starts a shell loop which read input from the user and parses the data.
	 * The loop is stopped when {@link #stop()} is called or when the "exit" command is called.
	 */
	public void run(){
		stop = false;
		String line = "";
		while (!stop) {
			line = readLine();
			parse(line);
		}
	}
	/**
	 * Stops the shell loop which runs when {@link #run()} is called.
	 */
	public void stop(){
		stop = true;
	}
}
