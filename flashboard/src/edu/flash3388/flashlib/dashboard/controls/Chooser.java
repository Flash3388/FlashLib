package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Chooser extends Displayable{

	private ComboBox<String> box;
	private VBox container = new VBox();
	private SimpleIntegerProperty selected = new SimpleIntegerProperty(-1);
	private boolean changed = false, manual = false;
	
	public Chooser(String name) {
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
		return DisplayType.Manual;
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(bytes[0] == 1){
			int sel = FlashUtil.toInt(bytes, 1);
			FlashFXUtils.onFXThread(()->{
				manual = true;
				box.getSelectionModel().select(sel);
				manual = false;
			});
			return;
		}
		String s = new String(bytes, 1, bytes.length - 1);
		String[] str = s.split(":");
		FlashFXUtils.onFXThread(()->{
			box.getItems().clear();
			box.getItems().addAll(str);
		});
	}
	@Override
	public byte[] dataForTransmition() {
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
