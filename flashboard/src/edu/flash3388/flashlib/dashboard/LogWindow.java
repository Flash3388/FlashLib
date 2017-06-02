package edu.flash3388.flashlib.dashboard;

import java.util.ArrayList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.gui.Dialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class LogWindow extends Stage{

	public static class RemoteLog extends Sendable{

		private ArrayList<String> logs = new ArrayList<String>();
		private LogWindow window;
		
		public RemoteLog(String name, int id) {
			super(name, id, FlashboardSendableType.LOG);
			LogWindow.newLog(this);
		}

		@Override
		public void newData(byte[] bytes) {
			String str = new String(bytes);
			logs.add(str);
			if(window != null)
				window.addLine(str);
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
	}
	
	private static ArrayList<RemoteLog> logs = new ArrayList<RemoteLog>();
	private static LogWindow currentInstance = null;
	
	private TextArea textArea;
	private TabPane tabs;
	private RemoteLog currentLog;
	
	private LogWindow(){
		setTitle("FLASHBoard - Log");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        
        setOnCloseRequest(new EventHandler<WindowEvent>(){
			@Override
			public void handle(WindowEvent event) {
				currentInstance = null;
			}
        });
	}
	
	private void updateTabs(RemoteLog log){
		Tab tab = new Tab(log.getName());
		tab.setClosable(false);
		tabs.getTabs().add(tab);
	}
	private void addLine(String line){
		textArea.appendText(line+"\n");
	}
	private void selectLog(int index){
		if(currentLog != null)
			currentLog.window = null;
		
		if(index < 0)
			currentLog = null;
		else{
			currentLog = logs.get(index);
			textArea.clear();
			
			String[] lines = currentLog.logs.toArray(new String[0]);
			for (String line : lines)
				textArea.appendText(line+"\n");
			currentLog.window = this;
		}
	}
	private Scene loadScene(){
		BorderPane root = new BorderPane();
		
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setPrefSize(300, 200);
		root.setCenter(textArea);
		BorderPane.setAlignment(root, Pos.CENTER);
		
		tabs = new TabPane();
		for (RemoteLog remoteLog : logs) {
			Tab tab = new Tab(remoteLog.getName());
			tab.setClosable(false);
			tabs.getTabs().add(tab);
		}
		if(tabs.getTabs().size() > 0){
			tabs.getSelectionModel().select(0);
			selectLog(0);
		}
		tabs.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				selectLog(newValue.intValue());
			}
		});
		root.setTop(tabs);
		
		
		return new Scene(root, 450, 300);
	}
	
	private static void newLog(RemoteLog log){
		logs.add(log);
		if(currentInstance != null)
			currentInstance.updateTabs(log);
	}
	
	public static void showLog(){
		currentInstance = new LogWindow();
		currentInstance.show();
		if(logs.size() < 1)
			Dialog.show(currentInstance, "Error", "No logs located");
	}
}