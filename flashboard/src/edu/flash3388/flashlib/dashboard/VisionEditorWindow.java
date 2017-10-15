package edu.flash3388.flashlib.dashboard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import edu.flash3388.flashlib.gui.FlashFXUtils;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.vision.*;
import javafx.geometry.Insets;
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
		private Object lastValue;
		private Class<T> genericType;
		private Pane root;
		
		ParamField(String name, Property<T> prop, Class<T> genericType) {
			field = new TextField();
			Label lbl = new Label(name);
			valueProperty = prop;
			this.genericType = genericType;
			
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
			if(!parseNewValue()){
				FlashFXUtils.showErrorDialog(instance, "Format Error", "Inputed value is incompatible of data type");
				field.setText(lastValue.toString());
			}
		}
		@SuppressWarnings("unchecked")
		private boolean parseNewValue(){
			String value = field.getText();
			Object newValue = null;
			if(FlashUtil.isAssignable(genericType, Double.class)){
				try{
					newValue = Double.parseDouble(value);
				}catch(NumberFormatException e){return false;}
			}
			if(FlashUtil.isAssignable(genericType, Integer.class)){
				try{
					newValue = Integer.parseInt(value);
				}catch(NumberFormatException e){return false;}
			}
			if(FlashUtil.isAssignable(genericType, Boolean.class)){
				try{
					newValue = Boolean.parseBoolean(value);
				}catch(NumberFormatException e){return false;}
			}
			if(FlashUtil.isAssignable(genericType, String.class)){
				newValue = value;
			}
			
			if(newValue != null && !newValue.equals(lastValue)){
				lastValue = newValue;
				valueProperty.setValue((T)lastValue);
			}
			return true;
		}
		@SuppressWarnings("unused")
		void checkValueChanged(){
			Object val = valueProperty.getValue();
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
	@SuppressWarnings("unused")
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
		reset();
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
		VisionFilter filter = visionObject.getProcessing().getFilter(idx);
		if(filter != null){
			getFilterParameters(params, filter);
			for (ParamField<?> field : params)
				paramRoot.getChildren().add(field.getRoot());
		}
	}
	private void addNewFilter(String type){
		VisionFilter filter = VisionFilter.createFilter(type, null);
		visionObject.getProcessing().addFilter(filter);
		filterView.getItems().add(type);
		filterView.getSelectionModel().select(filterView.getItems().size() - 1);
	}
	private void removeFilter(int idx){
		resetParams();
		visionObject.getProcessing().removeFilter(idx);
		filterView.getItems().remove(idx);
	}
	private void addNewProcessing(String name){
		VisionProcessing newProcObj = new VisionProcessing(name);
		procBox.getItems().add(newProcObj.getName());
		visionObject.addProcessing(newProcObj);
		visionObject.selectProcessing(visionObject.getProcessingCount() - 1);
		procBox.getSelectionModel().select(visionObject.getSelectedProcessingIndex() + 1);
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
			nameSelect.setTitle("New Vision Processing");
			nameSelect.setHeaderText("");
			Optional<String> result = nameSelect.showAndWait();
			if(result.isPresent())
				addNewProcessing(result.get());
		});
		top.setSpacing(10);
		top.setAlignment(Pos.CENTER);
		top.setPadding(new Insets(10, 0, 5, 0));
		top.getChildren().addAll(procBox, newProc);
		
		
		//center
		////filter
		HBox filterBox = new HBox();
		filterView = new ListView<String>();
		filterView.getSelectionModel().selectedIndexProperty().addListener((obs, o, n)->{
			if(procBox.getSelectionModel().getSelectedIndex() > 0 && n.intValue() >= 0)
				newFilterSelection(n.intValue());
		});
		Button addFilter = new Button("Add");
		addFilter.setOnAction(e -> {
			loadPossibleFilters();
			
			if(filterTypes.size() < 1){
				FlashFXUtils.showErrorDialog(this, "Filters Undetected", "Unable to find vision filters!"
						+ "\nPlease insure a filter creator is present and contains filter types");
			}
			ChoiceDialog<String> dialog = new ChoiceDialog<String>(filterTypes.get(0), filterTypes);
			dialog.setContentText("Select Filter Type");
			dialog.setTitle("New Filter Type");
			dialog.setHeaderText("");
			
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
		center.setPadding(new Insets(0, 10, 0, 10));
		
		
		//right
		right.setSpacing(5);
		right.setPadding(new Insets(0, 10, 0, 10));
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
		Map<String, Method> methods = VisionParam.findParametersMethods(filter.getClass());
		for (Iterator<Entry<String, Method>> entries = methods.entrySet().iterator(); entries.hasNext();) {
			Entry<String, Method> entry = entries.next();
			String name = entry.getKey();
			Method method = entry.getValue();
			
			Class<?> retType = method.getReturnType();
			if(retType == null)
				continue;
			
			Object obj = null;
			try {
				obj = method.invoke(filter);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
			if(obj == null || !(FlashUtil.isAssignable(retType, Property.class)))
				continue;
			
			Type[] genericTypes = FlashUtil.findGenericArgumentsOfSuperType(retType, Property.class);
			if(genericTypes == null || genericTypes.length < 1 || genericTypes[0] instanceof ParameterizedType)
				continue;
			
			Property<?> prop = (Property<?>)obj;
			Class<?> propType = (Class<?>)genericTypes[0];
			if(FlashUtil.isAssignable(propType, Boolean.class)){
				params.add(new ParamField<Boolean>(name, (Property<Boolean>)prop, Boolean.class));
			}
			else if(FlashUtil.isAssignable(propType, Double.class)){
				params.add(new ParamField<Double>(name, (Property<Double>)prop, Double.class));
			}
			else if(FlashUtil.isAssignable(propType, Integer.class)){
				params.add(new ParamField<Integer>(name, (Property<Integer>)prop, Integer.class));
			}
			else if(FlashUtil.isAssignable(propType, String.class)){
				params.add(new ParamField<String>(name, (Property<String>)prop, String.class));
			}
		}
	}
	
	public static void showEditor(Vision vision){
		if(instance != null){
			FlashFXUtils.showErrorDialog(GUI.getPrimary(), "Error", "Editor is already open!");
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
