package edu.flash3388.flashlib.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import edu.flash3388.flashlib.gui.FlashFxUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class LogWindowController implements Initializable{

	@FXML TextArea log_list;
	@FXML Button close_btn;
	@FXML MenuItem open_log, reset_log, close;
	
	private LogWindow window;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				window.close();
			}
		};
		close.setOnAction(handler);
		close_btn.setOnAction(handler);
		
		open_log.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				openlog();
			}
		});
		reset_log.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				openlog();
			}
		});
		
		log_list.setEditable(false);
	}
	
	private void openlog(){
		
	}
	
	public void set(LogWindow window){
		this.window = window;
	}
	public void log(String log){
		FlashFxUtils.onFxThread(()->{
			log_list.appendText(log+"\n");
		});
	}
}
