package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Controller extends Displayble{

	private static final int BAR_WIDTH = 100;
	private static final int BAR_HEIGHT = 10;
	private static final int LABEL_WIDTH = 80;
	private static final int LABEL_HEIGHT = 10;
	private static final int ELLIPSE_RADIUS = 8;
	
	private byte[] axes = new byte[6];
	private byte buttonCount = 0;
	private short buttons = 0;
	private byte[][] data = new byte[2][9];
	private int dataIndex = 0;
	private boolean firstUpdate = false, controlsNull = true, buttonControlsReady = false, updated = false;
	
	private Ellipse[] buttonControls;
	private HBox topNode = new HBox();
	private Label[] axesLabels;
	private ProgressBar[] axesBars;
	private Runnable updateRunnable;
	
	public Controller(String name, int id) {
		super(name, id, FlashboardSendableType.JOYSTICK);
		
		VBox all = new VBox();
		HBox[] axesControls = new HBox[6];
		axesLabels = new Label[6];
		axesBars = new ProgressBar[6];
		for(int i = 0; i < 6; i++){
			axesControls[i] = new HBox();
			axesControls[i].setSpacing(5);
			axesLabels[i] = new Label(i + "0");
			axesLabels[i].setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
			axesBars[i] = new ProgressBar(0.5);
			axesBars[i].setPrefSize(BAR_WIDTH, BAR_HEIGHT);
			axesControls[i].getChildren().addAll(axesLabels[i], axesBars[i]);
			all.getChildren().add(axesControls[i]);
		}
		topNode.getChildren().add(all);
		topNode.setSpacing(10);
		
		updateRunnable = new Runnable(){
			@Override
			public void run() {
				for(int i = 0; i < 6; i++){
					double val = getAxis(i);
					axesLabels[i].setText(i + ": " + val);
					axesBars[i].setProgress(val / 2 + 0.5);
				}
				if(buttonControls == null || !buttonControlsReady) return;
				for(int i = 0; i < buttonCount; i++){
					if(buttonControls[i] == null) return;
						buttonControls[i].setFill(getButton(i)? Color.YELLOWGREEN : Color.RED);
				}
			}
		};
	}

	public synchronized double getAxis(int axis){
		return Mathf.roundDecimal((axes[axis] < 0)? axes[axis] / 128.0 : axes[axis] / 127.0);
	}
	public synchronized boolean getButton(int button){
		return (buttons & (0x1 << button)) != 0;
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(bytes.length < data[1-dataIndex].length) return;
		System.arraycopy(bytes, 0, data[1-dataIndex], 0, data[1-dataIndex].length);
		if(!firstUpdate) firstUpdate = true;
		
		if(!updated){
			dataIndex ^= 1;
			updated = true;
		}
	}
	@Override
	public void update() {
		for(int i = 0; i < 6; i++)
			axes[i] = data[dataIndex][i];
		
		buttonCount = data[dataIndex][6];
		buttons = 0;
		buttons = ((short) data[dataIndex][7]);
		buttons |= (data[dataIndex][8] << (buttonCount / 2));
		
		updated = false;
		
		if(controlsNull && firstUpdate && buttonCount > 0){
			controlsNull = false;
			FlashFxUtils.onFxThread(()->{
				buttonControls = new Ellipse[buttonCount];
				HBox holder = new HBox();
				VBox left = new VBox(), right = new VBox();
				left.setSpacing(2); right.setSpacing(2);
				for(int i = 0; i < buttonCount / 2; i++){
					buttonControls[i] = new Ellipse();
					buttonControls[i].setFill(getButton(i)? Color.YELLOWGREEN : Color.RED);
					buttonControls[i].setRadiusX(ELLIPSE_RADIUS);
					buttonControls[i].setRadiusY(ELLIPSE_RADIUS);
					left.getChildren().add(buttonControls[i]);
					
					buttonControls[buttonCount / 2 + i] = new Ellipse();
					buttonControls[buttonCount / 2 + i].setFill(getButton(i)? Color.YELLOWGREEN : Color.RED);
					buttonControls[buttonCount / 2 + i].setRadiusX(ELLIPSE_RADIUS);
					buttonControls[buttonCount / 2 + i].setRadiusY(ELLIPSE_RADIUS);
					right.getChildren().add(buttonControls[buttonCount / 2 + i]);
				}
				holder.getChildren().addAll(left, right);
				holder.setSpacing(5);
				topNode.getChildren().add(holder);
				buttonControlsReady = true;
			});
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
	@Override
	public Runnable updateDisplay() {
		return updateRunnable;
	}
	@Override
	protected Node getNode(){return topNode;}
	@Override
	public DisplayType getDisplayType(){
		return DisplayType.Controller;
	}
}
