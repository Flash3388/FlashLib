package edu.flash3388.flashlib.gui;

import edu.flash3388.flashlib.dashboard.Remote;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChannelInputWindow extends Stage{
	
	private Remote.RemoteHost host;
	private Remote.User user;
	private TextField hField, uField, pField;
	private boolean ok = false;
	
	private ChannelInputWindow(){
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setScene(createScene());
		setOnCloseRequest((e)->cancel());
	}
	private void open(){
		String hostname = hField.getText();
		String username = uField.getText();
		String password = pField.getText();
		
		if(hostname.equals("")){
			Dialog.show(this, "Error", "Missing hostname");
			return;
		}
		if(username.equals("")){
			Dialog.show(this, "Error", "Missing username");
			return;
		}
		
		host = Remote.getRemoteHost(hostname);
		if(host == null){
			host = new Remote.RemoteHost(hostname);
			Remote.addRemoteHost(host);
		}
		user = host.getUser(username);
		if(user == null){
			user = new Remote.User(username, password);
			host.addUser(user);
		}
		ok = true;
		close();
	}
	private void cancel(){
		ok = false;
		close();
	}
	private Scene createScene(){
		
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.setAlignment(Pos.CENTER);
		hField = new TextField();
		hbox.getChildren().addAll(new Label("Hostname:"), hField);
		HBox ubox = new HBox();
		ubox.setSpacing(10);
		ubox.setAlignment(Pos.CENTER);
		uField = new TextField();
		ubox.getChildren().addAll(new Label("Username:"), uField);
		HBox pbox = new HBox();
		pbox.setSpacing(10);
		pbox.setAlignment(Pos.CENTER);
		pField = new TextField();
		pbox.getChildren().addAll(new Label("Password:"), pField);
		
		javafx.scene.control.Button open = new javafx.scene.control.Button("Open"), 
				cancel = new javafx.scene.control.Button("Cancel");
		open.setOnAction((e)->{
			open();
		});
		cancel.setOnAction((e)->{
			cancel();
		});
		
		BorderPane pane = new BorderPane();
		VBox center = new VBox();
		center.setSpacing(10);
		center.setPadding(new Insets(10, 10, 10, 10));
		center.getChildren().addAll(hbox, ubox, pbox);
		pane.setCenter(center);
		HBox bottom = new HBox();
		bottom.setSpacing(10);
		bottom.setAlignment(Pos.BOTTOM_RIGHT);
		bottom.setPadding(new Insets(0, 5, 5, 0));
		bottom.getChildren().addAll(open, cancel);
		pane.setBottom(bottom);
		return new Scene(pane, 300, 200);
	}
	
	public static Object[] showInput(){
		ChannelInputWindow in = new ChannelInputWindow();
		in.showAndWait();
		if(!in.ok)
			return null;
		return new Object[]{in.host, in.user};
	}
}
