package edu.flash3388.flashlib.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import edu.flash3388.flashlib.util.CommandParser.Command;

/**
 * Provides a platform for manually executing different code tests using an input stream. Each test must implement {@link CodeTest}
 * and added to the tester. It is possible to add new tests through the input stream.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CodeTester {

	/**
	 * Represents a test to be executed. All tests must implement this interface.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 */
	public static interface CodeTest{
		/**
		 * Gets the name of the test.
		 * 
		 * @return the name of the test.
		 */
		String getName();
		/**
		 * Runs the test with arguments specified by a string array.
		 * 
		 * @param args the array of arguments
		 * @param in the {@link java.util.Scanner} object used for user input
		 * @param out the {@link java.io.PrintStream} object used for user output
		 */
		void run(String[] args, Scanner in, PrintStream out);
	}
	
	private CommandParser parser;
	private HashMap<String, CodeTest> tests = new HashMap<String, CodeTest>();
	
	/**
	 * Creates a new instance of CodeTest which uses a given {@link java.util.Scanner} for user input and 
	 * {@link java.io.PrintStream} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all tests</li>
	 * 		<li>run: runs a given test</li>
	 * 		<li>exit: closes the tester's execution loop</li>
	 * </ul>
	 * @param in an input stream wrapped as {@link java.util.Scanner}
	 * @param out an output stream wrapped as {@link java.io.PrintStream}
	 */
	public CodeTester(Scanner in, PrintStream out){
		parser = new CommandParser("tester", in, out);
		loadDefaultCommands();
	}
	/**
	 * Creates a new instance of CodeTest which uses a given {@link java.io.InputStream} for user input and 
	 * {@link java.io.OutputStream} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all tests</li>
	 * 		<li>run: runs a given test</li>
	 * 		<li>exit: closes the tester's execution loop</li>
	 * </ul>
	 * @param in an input stream 
	 * @param out an output stream 
	 */
	public CodeTester(InputStream in, OutputStream out){
		this(new Scanner(in), new PrintStream(out));
	}
	/**
	 * Creates a new instance of CodeTest which uses {@link java.lang.System#in} for user input and 
	 * {@link java.lang.System#out} for user output.
	 * <p> 
	 * The instance is created with several default commands:
	 * <ul>
	 * 		<li>help: prints all tests</li>
	 * 		<li>run: runs a given test</li>
	 * 		<li>exit: closes the tester's execution loop</li>
	 * </ul>
	 */
	public CodeTester(){
		this(new Scanner(System.in), System.out);
	}
	
	private void loadDefaultCommands(){
		parser.addCommand(new Command(){
			@Override
			public String getName(){
				return "run";
			}
			@Override
			public String getDescription() {
				return "Runs a given test: run [test]";
			}
			@Override
			public void exec(String[] args, Scanner in, PrintStream out) {
				if(args.length < 1){
					out.println("Incorrect Syntax: Missing test name. \nUsage: run [test]");
					return;
				}
				
				String[] sArgs = null;
				if(args.length < 2)
					sArgs = new String[0];
				else sArgs = Arrays.copyOfRange(args, 1, args.length);
				runTest(args[0], sArgs);
			}
		});
		parser.addCommand(new Command(){
			@Override
			public String getName() {
				return "add";
			}
			@Override
			public String getDescription() {
				return "Adds a new test to the saved tests list: add [classname]";
			}
			@Override
			public void exec(String[] args, Scanner in, PrintStream out) {
				if (args.length < 1) 
					out.println("Incurrect Syntax: Missing class name to add. \nUsage: add[classname]");
				else addTest(args[0]);
			}
		});
		parser.addCommand(new Command(){
			@Override
			public String getName() {
				return "tests";
			}
			@Override
			public String getDescription() {
				return "Prints all the saved tests";
			}
			@Override
			public void exec(String[] args, Scanner in, PrintStream out) {
				String str = "";
				for (CodeTest codeTest : tests.values()) 
					str += "\t"+codeTest.getName();
				out.println(str);
			}
		});
	}
	
	/**
	 * Adds a command the can be executed from the input stream. If a command with the same name already exists, this command
	 * will not be saved.
	 * 
	 * @param command a {@link Command} to add
	 * @return true if the command was added, false otherwise
	 */
	public boolean addCommand(Command command){
		return parser.addCommand(command);
	}
	/**
	 * Gets a command with a given name from the command map, if one exists.
	 * 
	 * @param name the name of the command
	 * @return the {@link Command} with the given name, null if no such command exists
	 */
	public Command getCommand(String name){
		return parser.getCommand(name);
	}
	
	/**
	 * Adds a new test to the map that can be executed from the input stream. If a test with the same name exists, this test is not added. 
	 * @param test the {@link CodeTest} to add.
	 * @return true if the test was added, false otherwise.
	 */
	public boolean addTest(CodeTest test){
		if(getTest(test.getName()) != null)
			return false;
		tests.put(test.getName(), test);
		return true;
	}
	/**
	 * Adds a new test from a given {@link java.lang.Class} object. If the class does not implement {@link CodeTest}
	 * a runtime exception is thrown.
	 * 
	 * @param cl the {@link java.lang.Class} object of the test 
	 * @return true if the test was added, false otherwise.
	 * @throws IllegalArgumentException if the given {@link java.lang.Class} does not implement {@link CodeTest}
	 */
	public boolean addTest(Class<?> cl){
		if(!FlashUtil.isAssignable(cl, CodeTest.class))
			throw new IllegalArgumentException("Class does not implement CodeTest");
		
		try {
			CodeTest test = (CodeTest) cl.newInstance();
			return addTest(test);
		} catch (InstantiationException | IllegalAccessException e) {
			parser.println(e.getMessage());
			return false;
		}
	}
	/**
	 * Adds a new test from a given class name. If the class does not implement {@link CodeTest}
	 * a runtime exception is thrown.
	 * 
	 * @param className the name of the class 
	 * @return true if the test was added, false otherwise.
	 * @throws IllegalArgumentException if the given class name belongs to a class which does not implement {@link CodeTest}
	 */
	public boolean addTest(String className){
		Class<?> cl = null;
		try {
			cl = Class.forName(className);
			addTest(cl);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	/**
	 * Gets the test with the given name.
	 * 
	 * @param name the name of the test
	 * @return the test with the name give if it exists, null otherwise
	 */
	public CodeTest getTest(String name){
		return tests.get(name);
	}
	
	/**
	 * Starts a loop of IO with the user which read input from the user and parses the data.
	 */
	public void run(){
		parser.run();
	}
	/**
	 * Runs a test by its name with a given array of arguments. If the test is not saved in the test name, it is attempted
	 * to locate it by class name and package (assuming the given name is the class name and package). If finding the test
	 * has failed, error data is printed to the user output stream
	 * 
	 * @param testName the name of the test
	 * @param args a string array of arguments for the test
	 */
	public void runTest(String testName, String[] args){
		CodeTest test = getTest(testName);
		if(test == null){
			Class<?> cl = null;
			try {
				cl = Class.forName(testName);
			} catch (ClassNotFoundException e) {}
			if(cl != null){
				if(FlashUtil.isAssignable(cl, CodeTest.class)){
					try {
						test = (CodeTest) cl.newInstance();
						
						addTest(test);
						parser.println("Test added: "+testName);
					} catch (InstantiationException | IllegalAccessException e) {
						parser.println("Error: Unable to create instance for: "+cl.getName());
					}
				}else parser.println("Error: Class does not implement CodeTest");
			}
		}
		if(test == null){
			parser.println("Error: Test not found");
			return;
		}
		

		parser.println("Running Test: "+test.getName()+"\n");
		test.run(args, parser.getInputScanner(), parser.getPrintStream());
		parser.println("");
	}
	public void stop(){
		parser.stop();
	}
}
