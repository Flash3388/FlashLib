package edu.flash3388.flashlib.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import edu.flash3388.flashlib.util.CommandParser.Command;

public class CodeTester {

	public static interface CodeTest{
		String getName();
		void run(String[] args, Scanner in, PrintStream out);
	}
	
	private CommandParser parser;
	private List<CodeTest> tests = new ArrayList<CodeTest>();
	
	public CodeTester(Scanner in, PrintStream out){
		parser = new CommandParser("tester", in, out);
		loadDefaultCommands();
	}
	public CodeTester(InputStream in, OutputStream out){
		this(new Scanner(in), new PrintStream(out));
	}
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
				for (CodeTest codeTest : tests) 
					str += "\t"+codeTest.getName();
				out.println(str);
			}
		});
	}
	
	public boolean addCommand(Command command){
		return parser.addCommand(command);
	}
	public Command getCommand(String name){
		return parser.getCommand(name);
	}
	
	public boolean addTest(CodeTest test){
		if(getTest(test.getName()) != null)
			return false;
		tests.add(test);
		return true;
	}
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
	public CodeTest getTest(String name){
		for (CodeTest codeTest : tests) {
			if(codeTest.getName().equals(name))
				return codeTest;
		}
		return null;
	}
	
	public void run(){
		parser.run();
	}
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
