package edu.flash3388.flashlib.dashboard.controls;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.DashboardModeSelector;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.beans.property.SimpleBooleanProperty;

public class ModeSelectorControl extends Sendable{

	public static class State{
		public final int value;
		public final String name;
		
		State(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private static final State DISABLED = new State("Disabled", 0);
	
	private List<State> states;
	private int currentStateIndex;
	private javafx.beans.property.BooleanProperty disabled = new SimpleBooleanProperty(false);
	
	private boolean stateChanged = false;
	private boolean disabledChanged = false;
	
	public ModeSelectorControl() {
		super(FlashboardSendableType.MODE_SELECTOR);
		currentStateIndex = 0;
		states = new ArrayList<State>();
	}

	public void addState(String name, int value){
		addState(new State(name, value));
	}
	public void addState(State state){
		states.add(state);
	}
	public void removeState(int index){
		if(index == currentStateIndex)
			setDisabled(true);
		states.remove(index);
		
		if(!states.isEmpty())
			setCurrentState(index < states.size()? index : states.size() - 1);
	}
	
	public int getStatesCount(){
		return states.size();
	}
	public State getState(int state){
		return states.get(state);
	}
	
	public State getStateForName(String name){
		for (int i = 0; i < states.size(); i++) {
			if(states.get(i).name.equals(name))
				return states.get(i);
		}
		
		return null;
	}
	public State getStateForValue(int value){
		if(value == 0){
			return DISABLED;
		}
		
		for (int i = 0; i < states.size(); i++) {
			if(states.get(i).value == value)
				return states.get(i);
		}
		
		return null;
	}
	
	public int getCurrentState(){
		return currentStateIndex;
	}
	
	public boolean isDisabled(){
		return disabled.get();
	}
	
	public void setCurrentState(int state){
		if(state < 0 || state >= states.size())
			return;
		
		currentStateIndex = state;
		
		if(!disabled.get())
			stateChanged = true;
	}
	public void setDisabled(boolean disabled){
		this.disabled.set(disabled);
		if(!disabled){
			stateChanged = true;
		}
		disabledChanged = true;
	}
	
	public javafx.beans.property.BooleanProperty disabledProperty(){
		return disabled;
	}
	
	@Override
	public void newData(byte[] data) {
	}
	@Override
	public byte[] dataForTransmition() {
		if(stateChanged){
			stateChanged = false;
			byte[] bytes = new byte[5];
			bytes[0] = DashboardModeSelector.UPDATE_MODE;
			FlashUtil.fillByteArray(states.get(currentStateIndex).value, 1, bytes);
			return bytes;
		}
		disabledChanged = false;
		return new byte[]{DashboardModeSelector.UPDATE_DISABLED, (byte) (disabled.get()? 1 : 0)};
	}
	@Override
	public boolean hasChanged() {
		return stateChanged || disabledChanged;
	}

	@Override
	public void onConnection() {
		disabledChanged = true;
		disabled.set(true);
	}
	@Override
	public void onConnectionLost() {
		disabled.set(true);
	}
	
	
	public void loadStates(File file) throws Exception{
		states.clear();
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(file);
		
		doc.getDocumentElement().normalize();
		NodeList stateList = doc.getElementsByTagName("state");
		for (int i = 0; i < stateList.getLength(); i++) {
			Node node = stateList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				String name = element.getAttribute("name");
				
				if(getStateForName(name) != null)
					throw new RuntimeException("State name taken: "+name);
				
				String value = element.getAttribute("value");
				int bvalue = -1;
				
				try{
					bvalue = Integer.parseInt(value);
				}catch(NumberFormatException e){
					throw new RuntimeException("Value for state "+name+ " is not an int");
				}
				
				if(bvalue == 0)
					throw new RuntimeException("value for state "+name+" cannot be 0");
				
				if(getStateForValue(bvalue) != null)
					throw new RuntimeException("State value taken: "+bvalue);
				
				State state = new State(name, bvalue);
				states.add(state);
			}
		}
	}
	public void saveStates(File file){
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add("<?xml version=\"1.0\" ?>");
		lines.add("<state-selector>");
		for (int i = 0; i < states.size(); i++) {
			lines.add("\t<state name=\""+states.get(i).name+"\" value=\""+states.get(i).value+"\" />");
		}
		lines.add("</state-selector>");
		
		try {
			Files.write(file.toPath(), lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
