package edu.flash3388.flashlib.dashboard;

import java.io.IOException;
import java.util.ArrayList;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LogWindow extends Stage{

	public static class RemoteLog extends Sendable{

		public RemoteLog(int id) {
			super("", id, FlashboardSendableType.LOG);
		}

		@Override
		public void newData(byte[] bytes) {
			String str = new String(bytes);
			LogWindow.log(str);
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
	
	private static LogWindow instance;
	private static ArrayList<String> logs = new ArrayList<String>();
	
	private LogWindowController controller;
	
	private LogWindow(){
		setTitle("FLASHBoard - Log");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        
        for(String str : logs)
        	controller.log(str);
	}
	
	private Scene loadScene(){
		BorderPane root = new BorderPane();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("LogWindow.fxml"));
			loader.setRoot(root);
			root = loader.load();
			controller = loader.getController();
			controller.set(this);
			return new Scene(root, 450, 300);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void log(String log){
		logs.add(log);
		if(instance != null)
			instance.controller.log(log);
	}
	public static void showLog(){
		if(instance == null)
			instance = new LogWindow();
		
		if(!instance.isShowing())
			instance.show();
	}
	public static LogWindow getInstance(){
		return instance;
	}
}
