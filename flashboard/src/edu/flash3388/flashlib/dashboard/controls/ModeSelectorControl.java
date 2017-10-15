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
import edu.flash3388.flashlib.flashboard.FlashboardModeSelectorControl;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.beans.property.SimpleBooleanProperty;

public class ModeSelectorControl extends Sendable{

	public static class OpMode{
		public final int value;
		public final String name;
		
		OpMode(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
	
	private static final OpMode DISABLED = new OpMode("Disabled", 0);
	
	private List<OpMode> modes;
	private int currentModeIndex;
	private javafx.beans.property.BooleanProperty disabled = new SimpleBooleanProperty(false);
	
	private boolean modeChanged = false;
	private boolean disabledChanged = false;
	
	public ModeSelectorControl() {
		super(FlashboardSendableType.MODE_SELECTOR);
		currentModeIndex = 0;
		modes = new ArrayList<OpMode>();
	}

	public void addMode(String name, int value){
		addMode(new OpMode(name, value));
	}
	public void addMode(OpMode mode){
		modes.add(mode);
	}
	public void removeMode(int index){
		if(index == currentModeIndex)
			setDisabled(true);
		modes.remove(index);
		
		if(!modes.isEmpty())
			setCurrentMode(index < modes.size()? index : modes.size() - 1);
	}
	
	public int getModesCount(){
		return modes.size();
	}
	public OpMode getMode(int mode){
		return modes.get(mode);
	}
	
	public OpMode getModeForName(String name){
		for (int i = 0; i < modes.size(); i++) {
			if(modes.get(i).name.equals(name))
				return modes.get(i);
		}
		
		return null;
	}
	public OpMode getModeForValue(int value){
		if(value == 0){
			return DISABLED;
		}
		
		for (int i = 0; i < modes.size(); i++) {
			if(modes.get(i).value == value)
				return modes.get(i);
		}
		
		return null;
	}
	
	public int getCurrentMode(){
		return currentModeIndex;
	}
	
	public boolean isDisabled(){
		return disabled.get();
	}
	
	public void setCurrentMode(int state){
		if(state < 0 || state >= modes.size())
			return;
		
		currentModeIndex = state;
		
		if(!disabled.get())
			modeChanged = true;
	}
	public void setDisabled(boolean disabled){
		this.disabled.set(disabled);
		if(!disabled){
			modeChanged = true;
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
		if(modeChanged){
			modeChanged = false;
			byte[] bytes = new byte[5];
			bytes[0] = FlashboardModeSelectorControl.UPDATE_MODE;
			FlashUtil.fillByteArray(modes.get(currentModeIndex).value, 1, bytes);
			return bytes;
		}
		disabledChanged = false;
		return new byte[]{FlashboardModeSelectorControl.UPDATE_DISABLED, (byte) (disabled.get()? 1 : 0)};
	}
	@Override
	public boolean hasChanged() {
		return modeChanged || disabledChanged;
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
	
	
	public void loadModes(File file) throws Exception{
		modes.clear();
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(file);
		
		doc.getDocumentElement().normalize();
		NodeList stateList = doc.getElementsByTagName("mode");
		for (int i = 0; i < stateList.getLength(); i++) {
			Node node = stateList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				String name = element.getAttribute("name");
				
				if(getModeForName(name) != null)
					throw new RuntimeException("Mode name taken: "+name);
				
				String value = element.getAttribute("value");
				int bvalue = -1;
				
				try{
					bvalue = Integer.parseInt(value);
				}catch(NumberFormatException e){
					throw new RuntimeException("Value for mode "+name+ " is not an int");
				}
				
				if(bvalue == 0)
					throw new RuntimeException("value for mode "+name+" cannot be 0");
				
				if(getModeForValue(bvalue) != null)
					throw new RuntimeException("Mode value taken: "+bvalue);
				
				OpMode state = new OpMode(name, bvalue);
				modes.add(state);
			}
		}
	}
	public void saveModes(File file){
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add("<?xml version=\"1.0\" ?>");
		lines.add("<mode-selector>");
		for (int i = 0; i < modes.size(); i++) {
			lines.add("\t<mode name=\""+modes.get(i).name+"\" value=\""+modes.get(i).value+"\" />");
		}
		lines.add("</mode-selector>");
		
		try {
			Files.write(file.toPath(), lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
