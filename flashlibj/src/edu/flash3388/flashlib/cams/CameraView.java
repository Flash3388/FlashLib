package edu.flash3388.flashlib.cams;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

public class CameraView implements Camera{
	private Vector<Camera> cameras = new Vector<Camera>(4);
	private CameraViewSelector selector;
	private int currentIndex = -1;
	
	private static int instances = 0;
	
	public CameraView(String name, CameraViewSelector selector, Camera...cameras) {
		//super(name, Type.Camera);
		setSelector(selector);
		
		if(cameras != null){
			for(Camera camera : cameras)
				this.cameras.add(camera);
		}
	}
	public CameraView(CameraViewSelector selector, Camera...cameras) {
		this("Camera View" + instances, selector, cameras);
		instances++;
	}

	
	public void add(Camera camera){
		updateSelector(camera, true);
		cameras.add(camera);
	}
	public boolean remove(Camera camera){
		updateSelector(camera, false);
		return cameras.remove(camera);
	}
	public void remove(int index){
		updateSelector(index, index + 1, false);
		cameras.remove(index);
	}
	public Camera get(int index){
		return cameras.get(index);
	}
	public int getCamCount(){
		return cameras.size();
	}
	public void setSelector(CameraViewSelector sel){
		if(sel != null){
			selector = sel;
			updateSelector(0, getCamCount(), true);
		}
	}
	public CameraViewSelector getSelector(){
		return selector;
	}
	
	private void updateSelector(int start, int end, boolean add){
		if(selector == null) return;
		int c = 0;
		for(Enumeration<Camera> camEnum = cameras.elements(); c < end && camEnum.hasMoreElements(); c++){
			Camera cam = camEnum.nextElement();
			if(start > c) 
				continue;
			selector.newCam(cam);
		}
	}
	protected void updateSelector(Camera cam, boolean add){
		if(selector == null) return;
		if (add) selector.newCam(cam);
		else selector.remCam(cam);
	}
	
	public Camera currentCamera() {
		if(cameras.size() < 1) return null;
		int index = selector != null? selector.getCameraIndex() : 0;
		if(index < 0 || index >= cameras.size()){
			FlashUtil.getLog().reportError("Camera selector index is out of bounds "+index);
			return null;
		}
		if(currentIndex != index)
			currentIndex = index;
		return cameras.get(currentIndex);
	}
	@Override
	public int getQuality() {
		Camera c = currentCamera();
		if(c == null) return 0;
		return c.getQuality();
	}
	@Override
	public int getFPS() {
		Camera c = currentCamera();
		if(c == null) return 0;
		return c.getFPS();
	}
	@Override
	public void setFPS(int fps) {
		Camera c = currentCamera();
		if(c != null)
			c.setFPS(fps);
	}
	@Override
	public void setQuality(int quality) {
		Camera c = currentCamera();
		if(c != null)
			c.setQuality(quality);
	}
	@Override
	public byte[] getData() {
		Camera c = currentCamera();
		if(c == null) return null;
		return c.getData();
	}

}
