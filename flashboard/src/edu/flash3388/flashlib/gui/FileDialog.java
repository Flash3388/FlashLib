package edu.flash3388.flashlib.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FileDialog extends Stage{

	private String filepath, path, extension;
	private File[] files;
	private File file;
	private boolean success;
	private ListView<String> view;
	private TextField pathField;
	
	private FileDialog(Stage owner){
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		setOnCloseRequest((e)->cancel());
	}
	
	private void selectFile(int i){
		if(i < 0) return;
		filepath = path + files[i].getName();
		pathField.setText(filepath);
	}
	private void fieldPath(){
		view.getSelectionModel().select(-1);
		String f = pathField.getText();
		if(!f.startsWith(path))
			f = path + f;
		if(!extension.equals("") && !f.endsWith("."+extension))
			f = f + "."+extension;
		filepath = f;
	}
	private void load(){
		file = new File(filepath);
		if(!file.exists()){
			cancel();
			return;
		}
		success = true;
		close();
	}
	private void save(){
		String tx = pathField.getText();
		if(!tx.equals(filepath)) fieldPath();
		try {
			file = new File(filepath);
			if(file.exists())
				file.delete();
			file.createNewFile();
			success = true;
			close();
		} catch (IOException e) {
			cancel();
		}
	}
	private void cancel(){
		file = null;
		success = false;
		close();
	}
	private void createSaveFileScene(String filename, String savePath, String extension){
		setTitle("Save File");
		files = new File(savePath).listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("."+extension);
			}
		});
		view = new ListView<String>();
		pathField = new TextField();
		path = savePath;
		if(!path.endsWith("/")) path += "/";
		this.extension = extension;
		
		if(files == null)
			new File(savePath).mkdirs();
		else{
			for (File file : files)
				view.getItems().add(file.getName());
		}
		view.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			selectFile(newValue.intValue());
		});
		pathField.setText(savePath+filename+"."+extension); 
		pathField.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.ENTER)
				fieldPath();
		});
		
		javafx.scene.control.Button save = new javafx.scene.control.Button("Save"), 
				cancel = new javafx.scene.control.Button("Cancel");
		save.setOnAction((e)->{
			save();
		});
		cancel.setOnAction((e)->{
			cancel();
		});
		
		BorderPane pane = new BorderPane();
		VBox center = new VBox();
		center.setSpacing(10);
		center.setPadding(new Insets(10, 10, 10, 10));
		center.getChildren().addAll(view, pathField);
		pane.setCenter(center);
		HBox bottom = new HBox();
		bottom.setSpacing(10);
		bottom.setAlignment(Pos.BOTTOM_RIGHT);
		bottom.setPadding(new Insets(0, 5, 5, 0));
		bottom.getChildren().addAll(save, cancel);
		pane.setBottom(bottom);
		setScene(new Scene(pane, 300, 200));
	}
	private void createLoadFileScene(String loadPath, String extension){
		setTitle("Load File");
		files = new File(loadPath).listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("."+extension);
			}
		});
		view = new ListView<String>();
		pathField = new TextField();
		path = loadPath;
		if(!path.endsWith("/")) path += "/";
		this.extension = extension;
		
		if(files == null)
			new File(loadPath).mkdirs();
		else{
			for (File file : files)
				view.getItems().add(file.getName());
		}
		view.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			selectFile(newValue.intValue());
		});
		pathField.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.ENTER)
				fieldPath();
		});
		
		javafx.scene.control.Button load = new javafx.scene.control.Button("Load"), 
				cancel = new javafx.scene.control.Button("Cancel");
		load.setOnAction((e)->{
			load();
		});
		cancel.setOnAction((e)->{
			cancel();
		});
		
		BorderPane pane = new BorderPane();
		VBox center = new VBox();
		center.setSpacing(10);
		center.setPadding(new Insets(10, 10, 10, 10));
		center.getChildren().addAll(view, pathField);
		pane.setCenter(center);
		HBox bottom = new HBox();
		bottom.setSpacing(10);
		bottom.setAlignment(Pos.BOTTOM_RIGHT);
		bottom.setPadding(new Insets(0, 5, 5, 0));
		bottom.getChildren().addAll(load, cancel);
		pane.setBottom(bottom);
		setScene(new Scene(pane, 300, 200));
	}
	private void loadMultipleFileChooser(){
		setTitle("Load Files");
		DirectoryChooser chooser = new DirectoryChooser();
		view = new ListView<String>();
		pathField = new TextField();
		
		javafx.scene.control.Button load = new javafx.scene.control.Button("Load"), 
				cancel = new javafx.scene.control.Button("Cancel"), browse = new javafx.scene.control.Button("Browse");
		load.setOnAction((e)->load());
		cancel.setOnAction((e)->cancel()); 
		browse.setOnAction((e)->{
			File res = chooser.showDialog(this);
			filepath = res.getAbsolutePath();
			files = new File(filepath).listFiles(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("."+extension);
				}
			});
			view.getItems().clear();
			for (File file : files)
				view.getItems().add(file.getName());
			pathField.setText(filepath);
		});
		
		BorderPane pane = new BorderPane();
		VBox center = new VBox();
		HBox directory = new HBox();
		directory.setSpacing(5);
		directory.getChildren().addAll(pathField, browse);
		center.setSpacing(10);
		center.setPadding(new Insets(10, 10, 10, 10));
		center.getChildren().addAll(view, directory);
		pane.setCenter(center);
		HBox bottom = new HBox();
		bottom.setSpacing(10);
		bottom.setAlignment(Pos.BOTTOM_RIGHT);
		bottom.setPadding(new Insets(0, 5, 5, 0));
		bottom.getChildren().addAll(load, cancel);
		pane.setBottom(bottom);
		setScene(new Scene(pane, 300, 200));
	}
	
	public static File showLoadDialog(Stage owner, String path, String extension){
		FileDialog d = new FileDialog(owner);
		d.createLoadFileScene(path, extension);
		d.showAndWait();
		return d.success? d.file : null;
	}
	public static File showSaveDialog(Stage owner, String path, String name, String extension){
		FileDialog d = new FileDialog(owner);
		d.createSaveFileScene(name, path, extension);
		d.showAndWait();
		return d.success? d.file : null;
	}
	public static File showDirectoryChooser(Stage owner){
		FileDialog d = new FileDialog(owner);
		d.loadMultipleFileChooser();
		d.showAndWait();
		return d.success? d.file : null;
	}
}
