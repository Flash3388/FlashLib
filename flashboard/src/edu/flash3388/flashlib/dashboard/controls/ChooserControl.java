package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ChooserControl extends Displayable{

	private ComboBox<String> box;
	private VBox container = new VBox();
	private SimpleIntegerProperty selected = new SimpleIntegerProperty(-1);
	private boolean changed = false, manual = false;
	
	public ChooserControl(String name) {
		super(name, FlashboardSendableType.CHOOSER);
		
		box = new ComboBox<String>();
		container.getChildren().addAll(new Label(name), box);
		container.setSpacing(5);
		box.valueProperty().addListener((obs, o, n)->{
			synchronized (selected) {
				selected.set(box.getSelectionModel().getSelectedIndex());
				if(!manual)
					changed = true;
			}
		});
	}

	@Override
	protected Node getNode(){
		return container;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Input;
	}
	
	@Override
	public void newData(byte[] bytes) throws SendableException {
		if(bytes[0] == 1){
			int sel = FlashUtil.toInt(bytes, 1);
			FlashFXUtils.onFXThread(()->{
				manual = true;
				box.getSelectionModel().select(sel);
				manual = false;
			});
			return;
		}
		else if(bytes[0] == 0){
			final int index = bytes[1];
			final String s = new String(bytes, 2, bytes.length - 2);
			FlashFXUtils.onFXThread(()->{
				if(box.getItems().size() <= index)
					box.getItems().add(index, s);
				else
					box.getItems().set(index, s);
			});
		}
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		changed = false;
		return FlashUtil.toByteArray(selected.get());
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
}
