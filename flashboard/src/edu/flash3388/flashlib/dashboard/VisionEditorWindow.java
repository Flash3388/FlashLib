package edu.flash3388.flashlib.dashboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.vision.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VisionEditorWindow extends Stage{
	
	private static class ParamField<T>{
		private TextField field;
		private Property<T> valueProperty;
		private T lastValue;
		private Pane root;
		
		ParamField(String name, Property<T> prop) {
			field = new TextField();
			Label lbl = new Label(name);
			valueProperty = prop;
			
			lastValue = prop.getValue();
			field.setText(lastValue.toString());
			field.setOnKeyPressed(e ->{
				if(e.getCode() == KeyCode.ENTER)
					newValue();
			});
			field.focusedProperty().addListener((obs, o, n)->{
				if(o.booleanValue() && !n.booleanValue())
					newValue();
			});
			
			root = new VBox();
			root.getChildren().addAll(lbl, field);
		}
		
		private void newValue(){
			
		}
		void checkValueChanged(){
			T val = valueProperty.getValue();
			if(!val.equals(lastValue)){
				lastValue = val;
				field.setText(val.toString());
			}
		}
		Pane getRoot(){
			return root;
		}
	}
	
	private static VisionEditorWindow instance = null;
	
	private Vision visionObject;
	private Runnable updateListener;
	private List<String> filterTypes;
	
	private VBox paramRoot;
	private List<ParamField<?>> params;
	private ListView<String> filterView;
	private ComboBox<String> procBox;
	
	private VisionEditorWindow(Vision vision){
		this.visionObject = vision;
		
		setTitle("FLASHboard - Vision Editor");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	instance = null;
        });
	}
	
	private void reset(){
		filterView.getItems().clear();
		resetParams();
	}
	private void resetParams(){
		paramRoot.getChildren().clear();
		if(params != null)
			params.clear();
	}
	private void newSelection(int index){
		visionObject.selectProcessing(index);
		VisionFilter[] filters = visionObject.getProcessing().getFilters();
		for (VisionFilter filter : filters) {
			if(filter == null)
				continue;
			String name = VisionFilter.getSaveName(filter);
			filterView.getItems().add(name);
		}
	}
	private void newFilterSelection(int idx){
		resetParams();
		getFilterParameters(params, visionObject.getProcessing().getFilter(idx));
		for (ParamField<?> field : params)
			paramRoot.getChildren().add(field.getRoot());
	}
	private void addNewFilter(String type){
		VisionFilter filter = VisionFilter.createFilter(type, null);
		visionObject.getProcessing().addFilter(filter);
		filterView.getItems().add(type);
		filterView.getSelectionModel().select(filterView.getItems().size() - 1);
	}
	private void removeFilter(int idx){
		visionObject.getProcessing().removeFilter(idx);
		filterView.getItems().remove(idx);
	}
	private void addNewProcessing(String name){
		VisionProcessing newProcObj = new VisionProcessing(name);
		procBox.getItems().add(newProcObj.getName());
		procBox.getSelectionModel().select(visionObject.getSelectedProcessingIndex());
		visionObject.addProcessing(newProcObj);
		visionObject.selectProcessing(visionObject.getProcessingCount() - 1);
	}
	
	private Scene loadScene(){
		HBox top = new HBox();
		VBox right = new VBox();
		VBox center = new VBox();
		//HBox bottom = new HBox();
		
		BorderPane root = new BorderPane();
		//root.setBottom(bottom);
		root.setTop(top);
		root.setCenter(center);
		root.setRight(right);
		
		//top
		procBox = new ComboBox<String>();
		procBox.getItems().add("----SELECT VISION----");
		procBox.getSelectionModel().select(0);
		procBox.getSelectionModel().selectedIndexProperty().addListener((obs, o, n)->{
			int selected = n.intValue();
			if(selected == 0)
				reset();
			else 
				newSelection(selected - 1);
		});
		Button newProc = new Button("New");
		newProc.setOnAction((e)->{
			TextInputDialog nameSelect = new TextInputDialog("vision");
			nameSelect.initOwner(this);
			nameSelect.setContentText("Enter processing name");
			Optional<String> result = nameSelect.showAndWait();
			if(result.isPresent())
				addNewProcessing(result.get());
		});
		top.setSpacing(10);
		top.setAlignment(Pos.CENTER);
		top.getChildren().addAll(procBox, newProc);
		
		
		//center
		////filter
		HBox filterBox = new HBox();
		filterView = new ListView<String>();
		filterView.getSelectionModel().selectedIndexProperty().addListener((obs, o, n)->{
			newFilterSelection(n.intValue());
		});
		Button addFilter = new Button("Add");
		addFilter.setOnAction(e -> {
			loadPossibleFilters();
			
			if(filterTypes.size() < 1){
				FlashFxUtils.showErrorDialog(this, "Filters Undetected", "Unable to find vision filters!"
						+ "\nPlease insure a filter creator is present and contains filter types");
			}
			ChoiceDialog<String> dialog = new ChoiceDialog<String>(filterTypes.get(0), filterTypes);
			dialog.setContentText("Select Filter Type");
			
			Optional<String> result = dialog.showAndWait();
			if(result.isPresent())
				addNewFilter(result.get());
		});
		Button remFilter = new Button("Remove");
		remFilter.setOnAction(e -> {
			int selIdx = filterView.getSelectionModel().getSelectedIndex();
			if(selIdx >= 0)
				removeFilter(selIdx);
		});
		
		VBox filtersButtons = new VBox();
		filtersButtons.setAlignment(Pos.BOTTOM_CENTER);
		filtersButtons.setSpacing(5);
		filtersButtons.getChildren().addAll(addFilter, remFilter);
		
		filterBox.setSpacing(5);
		filterBox.getChildren().addAll(filterView, filtersButtons);
		
		////creator
		
		
		center.setSpacing(10);
		center.getChildren().addAll(filterBox);
		
		
		//right
		right.setSpacing(5);
		paramRoot = right;
		params = new ArrayList<ParamField<?>>();
		
		//bottom
		
		
		reset();
		for (int i = 0; i < visionObject.getProcessingCount(); i++)
			procBox.getItems().add(visionObject.getProcessing(i).getName());
		procBox.getSelectionModel().select(visionObject.getSelectedProcessingIndex() + 1);
		
		return new Scene(root, 800, 500);
	}
	
	private void loadPossibleFilters(){
		if(filterTypes != null)
			return;
		filterTypes = new ArrayList<String>();
		
		FilterCreator creator = VisionFilter.getFilterCreator();
		String[] types = creator.getFiltersMap().keySet().toArray(new String[creator.getFiltersMap().size()]);
		for (String type : types)
			filterTypes.add(type);
	}
	
	@SuppressWarnings("unchecked")
	private static void getFilterParameters(List<ParamField<?>> params, VisionFilter filter){
		Method[] methods = filter.getClass().getMethods();
		for (Method method : methods) {
			int idx = method.getName().indexOf("Property");
			if(idx >= 0 && idx + 8 == method.getName().length()){
				String propName = method.getName().substring(0, idx);
				
				Object returnVal = null;
				try {
					returnVal = method.invoke(filter);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					continue;
				}
				
				if(returnVal != null && returnVal instanceof Property){
					ParamField<?> param = null;
					
					ParameterizedType superType = (ParameterizedType) returnVal.getClass().getGenericSuperclass();
					Class<?> typeClass = (Class<?>) superType.getActualTypeArguments()[0];
					if(FlashUtil.isAssignable(typeClass, Double.class))
						param = new ParamField<Double>(propName, (Property<Double>)returnVal);
					else if(FlashUtil.isAssignable(typeClass, String.class))
						param = new ParamField<String>(propName, (Property<String>)returnVal);
					else if(FlashUtil.isAssignable(typeClass, Integer.class))
						param = new ParamField<Integer>(propName, (Property<Integer>)returnVal);
					else if(FlashUtil.isAssignable(typeClass, Boolean.class))
						param = new ParamField<Boolean>(propName, (Property<Boolean>)returnVal);
					
					if(param != null)
						params.add(param);
				}
			}
		}
	}
	
	public static void showEditor(Vision vision){
		if(instance != null){
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Editor is already open!");
			return;
		}
		instance = new VisionEditorWindow(vision);
		instance.show();
	}
	public static void closeEditor(){
		if(instance != null){
			instance.close();
			instance = null;
		}
	}
}
