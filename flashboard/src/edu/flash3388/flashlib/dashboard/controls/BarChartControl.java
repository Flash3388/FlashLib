package edu.flash3388.flashlib.dashboard.controls;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardBarChart;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.FlashUtil;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class BarChartControl extends Displayable{

	public static class BarChartSeriesControl extends Displayable{

		private double data = 0.0;
		private boolean dataChanged = false;
		private Object dataMutex = new Object();
		private String chartName = null;
		
		public BarChartSeriesControl(String name) {
			super(name, FlashboardSendableType.BARCHART_SERIES);
		}

		public String getChartName(){
			return chartName;
		}
		public double getData(){
			double r = 0.0;
			synchronized (dataMutex) {
				r = data;
				dataChanged = false;
			}
			return r;
		}
		public boolean hasData(){
			boolean r = false;
			synchronized (dataMutex) {
				r = dataChanged;
			}
			return r;
		}
		
		@Override
		public void newData(byte[] data) {
			if(data[0] == FlashboardBarChart.CHART_NAME_UPDATE){
				chartName = new String(data, 1, data.length - 1);
				BarChartControl.allocateSeries(this);
			}
			if(data[0] == FlashboardBarChart.DATA_UPDATE){
				double value = FlashUtil.toDouble(data, 1);
				synchronized (dataMutex) {
					this.data = value;
					dataChanged = true;
				}
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
		public void onConnection() {
		}
		@Override
		public void onConnectionLost() {
		}
	}

	public static final double WIDTH = 60.0;
	public static final double HEIGHT = 50.0;
	
	private static HashMap<String, List<BarChartSeriesControl>> unallocatedControls = 
			new HashMap<String, List<BarChartSeriesControl>>();
	private static HashMap<String, BarChartControl> charts = new HashMap<String, BarChartControl>();
	
	private Vector<BarChartSeriesControl> controls = new Vector<BarChartSeriesControl>();
	
	private VBox root;
	
	private BarChart<String,Number> chart;
	private BarChart.Series<String,Number> chartSeries;
	private CategoryAxis axisX;
	private NumberAxis axisY;
	
	private Object configMutex = new Object();
	private double minY = 0.0, maxY = 1.0;
	private boolean configUpdate = false;
	
	public BarChartControl(String name) {
		super(name, FlashboardSendableType.BARCHART);
		
		axisX = new CategoryAxis();
		axisX.setAutoRanging(true);
		axisY = new NumberAxis();
		axisY.setForceZeroInRange(false);
		axisY.setAutoRanging(false);
		axisY.setUpperBound(maxY);
		axisY.setLowerBound(minY);
		
		chart = new BarChart<String, Number>(axisX, axisY);
		chart.setLegendVisible(false);
		chart.setAnimated(false);
		
		chartSeries = new BarChart.Series<String, Number>();
		chart.setTitle(name);
		chart.getData().add(chartSeries);
		
		root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setMaxSize(WIDTH, HEIGHT);
		root.getChildren().add(chart);
		
		
		charts.put(name, this);
		List<BarChartSeriesControl> controls = unallocatedControls.get(name);
		if(unallocatedControls != null){
			unallocatedControls.remove(name);
			for (int i = 0; i < unallocatedControls.size(); i++)
				addControl(controls.get(i));
		}
	}

	public void addControl(BarChartSeriesControl control){
		controls.addElement(control);
	}
	
	@Override
	protected Node getNode() {
		return root;
	}
	@Override
	protected DisplayType getDisplayType() {
		return DisplayType.GraphicData;
	}
	@Override
	protected void update() {
		synchronized (configMutex) {
			if(configUpdate){
				configUpdate = false;
				
				axisY.setUpperBound(maxY);
				axisY.setLowerBound(minY);
			}
		}
		
		Enumeration<BarChartSeriesControl> controlEnum = controls.elements();
		while(controlEnum.hasMoreElements()){
			BarChartSeriesControl control = controlEnum.nextElement();
			
			if(control.hasData()){
				double dataY = Mathf.roundDecimal(control.getData());
				Data<String, Number> data = new Data<String, Number>(control.getName(), dataY);
				
				//chart.setTitle(getName()+": "+dataY);
				chartSeries.getData().add(data);
				((StackPane)data.getNode()).getChildren().add(new Label(String.valueOf(dataY)));
			}
		}
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == FlashboardBarChart.CONFIG_UPDATE){
			synchronized (configMutex) {
				minY = FlashUtil.toDouble(data, 1);
				maxY = FlashUtil.toDouble(data, 9);
				configUpdate = true;
			}
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
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
	
	
	private static void allocateSeries(BarChartSeriesControl series){
		String name = series.getChartName();
		if(name == null || name.equals("")){
			return;
		}else{
			BarChartControl chart = charts.get(name);
			if(chart != null)
				chart.addControl(series);
			else{
				List<BarChartSeriesControl> controls = unallocatedControls.get(name);
				if(controls == null){
					controls = new ArrayList<BarChartSeriesControl>();
					unallocatedControls.put(name, controls);
				}
				controls.add(series);
			}
		}
	}
	public static void resetControls(){
		unallocatedControls.clear();
		charts.clear();
	}
}
