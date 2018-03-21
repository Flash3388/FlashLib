package edu.flash3388.flashlib.cams;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Implements a camera interface for multiple cameras.
 * Allows for a selection of a single camera to get data from 
 * the cameras using {@link CameraViewSelector}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class CameraView implements Camera{
	
	private Vector<Camera> cameras = new Vector<Camera>(4);
	private CameraViewSelector selector;
	private int currentIndex = -1;
	
	private static int instances = 0;
	
	/**
	 * Creates a new multiple camera holder with a collection of {@link Camera} objects
	 * and a {@link CameraViewSelector}.
	 * 
	 * @param name the name of the camera view
	 * @param selector the selector of the current camera
	 * @param cameras cameras to add 
	 */
	@SafeVarargs
	public CameraView(String name, CameraViewSelector selector, Camera...cameras) {
		setSelector(selector);
		
		if(cameras != null){
			for(Camera camera : cameras)
				this.cameras.add(camera);
		}
	}
	/**
	 * Creates a new multiple camera holder with a collection of {@link Camera} objects
	 * and a {@link CameraViewSelector}.
	 * 
	 * @param selector the selector of the current camera
	 * @param cameras cameras to add 
	 */
	@SafeVarargs
	public CameraView(CameraViewSelector selector, Camera...cameras) {
		this("Camera View" + instances, selector, cameras);
		instances++;
	}

	/**
	 * Adds a new camera to the camera collection and updates the selector.
	 * @param camera the new camera
	 * @see CameraViewSelector#newCam(Camera)
	 */
	public void add(Camera camera){
		updateSelector(camera, true);
		cameras.add(camera);
	}
	/**
	 * Adds new cameras to the camera collection and updates the selector.
	 * @param cameras new cameras to add
	 * @see #add(Camera)
	 */
	public void add(Camera... cameras){
		for (Camera camera : cameras)
			add(camera);
	}
	/**
	 * Removes a camera from the camera collection and updates the selector.
	 * @param camera camera to remove
	 * @return true if the camera was successfully removed
	 * @see CameraViewSelector#remCam(Camera)
	 */
	public boolean remove(Camera camera){
		updateSelector(camera, false);
		return cameras.remove(camera);
	}
	/**
	 * Removes a camera from the camera collection and updates the selector.
	 * @param index the index of the camera to remove
	 * @see CameraViewSelector#remCam(Camera)
	 */
	public void remove(int index){
		updateSelector(index, index + 1, false);
		cameras.remove(index);
	}
	/**
	 * Gets the camera from the collection by an index.
	 * @param index the index of the camera
	 * @return a camera at the collection of cameras
	 */
	public Camera get(int index){
		return cameras.get(index);
	}
	/**
	 * Gets the count of cameras in the collection
	 * @return the amount of cameras
	 */
	public int getCamCount(){
		return cameras.size();
	}
	
	/**
	 * Sets the {@link CameraViewSelector} to use by this instance. If a selector was
	 * already set, the new selector will be ignored.
	 * @param sel the new selector
	 */
	public void setSelector(CameraViewSelector sel){
		if(sel != null){
			selector = sel;
			updateSelector(0, getCamCount(), true);
		}
	}
	/**
	 * Gets the current selector used by this instance.
	 * @return the current selector, or null if not set.
	 */
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
	
	/**
	 * Gets the currently selected camera from the {@link CameraViewSelector}. If no selector is set,
	 * the first camera is selected.
	 * 
	 * @return the currently selected camera
	 */
	public Camera currentCamera() {
		if(cameras.size() < 1) 
			return null;
		
		int index = selector != null? selector.getCameraIndex() : 0;
		if(index < 0 || index >= cameras.size()){
			FlashUtil.getLogger().severe("Camera selector index is out of bounds: "+index);
			return null;
		}
		
		if(currentIndex != index)
			currentIndex = index;
		return cameras.get(currentIndex);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and gets its {@link Camera#getQuality()}.
	 * </p>
	 */
	@Override
	public int getQuality() {
		Camera c = currentCamera();
		if(c == null) return 0;
		return c.getQuality();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and gets its {@link Camera#getFPS()}.
	 * </p>
	 */
	@Override
	public int getFPS() {
		Camera c = currentCamera();
		if(c == null) return 0;
		return c.getFPS();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and sets its {@link Camera#setFPS(int)}.
	 * </p>
	 */
	@Override
	public void setFPS(int fps) {
		Camera c = currentCamera();
		if(c != null)
			c.setFPS(fps);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and sets its {@link Camera#setQuality(int)}.
	 * </p>
	 */
	@Override
	public void setQuality(int quality) {
		Camera c = currentCamera();
		if(c != null)
			c.setQuality(quality);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and gets its {@link Camera#getData()}.
	 * </p>
	 */
	@Override
	public byte[] getData() {
		Camera c = currentCamera();
		if(c == null) return null;
		return c.getData();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the current camera from {@link #currentCamera()} and gets its {@link Camera#read()}.
	 * </p>
	 */
	@Override
	public Object read() {
		Camera c = currentCamera();
		if(c == null) return null;
		return c.read();
	}
}
