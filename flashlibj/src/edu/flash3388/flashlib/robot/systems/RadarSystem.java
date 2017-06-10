package edu.flash3388.flashlib.robot.systems;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.math.Vector2;
import edu.flash3388.flashlib.robot.devices.RangeFinder;
import edu.flash3388.flashlib.util.FlashUtil;

public class RadarSystem{
	public static class RadarNode{
		public static final double DEFAULT_MAX_RANGE = 400;//cm
		private double angle;
		private RangeFinder ranger;
		private double range = 0, maxRange;
		
		public RadarNode(RangeFinder ranger, double angle, double maxRange){
			this.angle = angle;
			this.ranger = ranger;
			this.maxRange = maxRange;
		}
		public RadarNode(RangeFinder ranger, double angle){
			this(ranger, angle, DEFAULT_MAX_RANGE);
		}
		
		public double getAngleOffset(){
			return angle;
		}
		public RangeFinder getRangeFinder(){
			return ranger;
		}
		public void ping(){
			ranger.ping();
		}
		public double getRange(){
			return range;
		}
		public void updateRange(){
			range = ranger.getRangeCM();
			if(range > maxRange)
				range = -1;
		}
	}
	public static class UpdateTask implements Runnable{
		private boolean stop = false;
		private RadarSystem radar;

		public UpdateTask(RadarSystem radar){
			this.radar = radar;
		}
		@Override
		public void run() {
			while(!stop){
				radar.update();
			}
		}
		public void stop(){
			stop = true;
		}
	}
	
	private Vector<RadarNode> nodes = new Vector<RadarNode>();
	private UpdateTask task = new UpdateTask(this);
	private Thread updateThread;
 	
	public RadarSystem(){
		updateThread = new Thread(task);
	}
	
	public void addNode(RangeFinder finder, double angle){
		addNode(new RadarNode(finder, angle));
	}
	public void addNode(RadarNode node){
		if(getNodeAtOffset(node.getAngleOffset()) != null){
			return;
		}
		nodes.addElement(node);
	}
	public void addNodes(RadarNode...nodes){
		for (RadarNode radarNode : nodes)
			addNode(radarNode);
	}
	public boolean removeNode(RadarNode node){
		return nodes.remove(node);
	}
	public RadarNode removeNode(int index){
		return nodes.remove(index);
	}
	public boolean removeNodeAtOffset(double angle){
		RadarNode node = getNodeAtOffset(angle);
		if(node == null) return false;
		return nodes.remove(node);
	}
	public int getNodeCount(){
		return nodes.size();
	}
	public void clear(){
		nodes.clear();
	}
	public RadarNode getNode(int index){
		return nodes.get(index);
	}
	public RadarNode getNodeAtOffset(double angle){
		for (Enumeration<RadarNode> nodeEnum = nodes.elements(); nodeEnum.hasMoreElements();){
			RadarNode radarNode = nodeEnum.nextElement();
			if(radarNode.getAngleOffset() == angle)
				return radarNode;
		}
		return null;
	}
	public RadarNode getClosestNodeTo(double angle){
		RadarNode bestNode = null;
		double closestOffset = -1;
		for (Enumeration<RadarNode> nodeEnum = nodes.elements(); nodeEnum.hasMoreElements();){
			RadarNode radarNode = nodeEnum.nextElement();
			double offset = Math.abs(radarNode.getAngleOffset() - angle);
			if(offset == 0)
				return radarNode;
			if(closestOffset < 0 || offset < closestOffset){
				bestNode = radarNode;
				closestOffset = offset;
			}
		}
		return bestNode;
	}
	
	public double getRangeAt(double angle){
		RadarNode node = getNodeAtOffset(angle);
		if(node != null)
			return node.getRange();
		return -1;
	}
	public Vector2 getClosestRangeTo(double angle){
		RadarNode node = getClosestNodeTo(angle);
		if(node != null) 
			return Vector2.polar(node.getRange(), node.getAngleOffset());
		return null;
	}
	public Vector2[] getRanges(){
		Vector2[] ranges = new Vector2[nodes.size()];
		int i = 0;
		for (Enumeration<RadarNode> nodeEnum = nodes.elements(); nodeEnum.hasMoreElements();){
			RadarNode node = nodeEnum.nextElement();
			ranges[i++] = Vector2.polar(node.getRange(), node.getAngleOffset());
		}
		return ranges;
	}
	public void update(){
		for (Enumeration<RadarNode> nodeEnum = nodes.elements(); nodeEnum.hasMoreElements();){
			RadarNode radarNode = nodeEnum.nextElement();
			radarNode.ping();
			FlashUtil.delay(1);
			radarNode.updateRange();
		}
	}
	public void enableAutomaticUpdate(){
		if(!updateThread.isAlive())
			updateThread.start();
	}
	public void stopAutomaticUpdate(){
		if (updateThread.isAlive())
			task.stop();
	}
}
