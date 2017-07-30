package edu.flash3388.flashlib.robot;

public interface HIDInterface {

	public static class EmptyHIDInterface implements HIDInterface{

		@Override
		public boolean isHIDConnected(int hid) {
			return false;
		}
		@Override
		public boolean isAxisConnected(int hid, int axis) {
			return false;
		}
		@Override
		public boolean isPOVConnected(int hid, int pov) {
			return false;
		}
		@Override
		public boolean isButtonConnected(int hid, int button) {
			return false;
		}

		@Override
		public double getHIDAxis(int hid, int axis) {
			return 0;
		}
		@Override
		public boolean getHIDButton(int hid, int button) {
			return false;
		}
		@Override
		public int getHIDPOV(int hid, int pov) {
			return -1;
		}
		
	}
	
	boolean isHIDConnected(int hid);
	boolean isAxisConnected(int hid, int axis);
	boolean isPOVConnected(int hid, int pov);
	boolean isButtonConnected(int hid, int button);
	
	double getHIDAxis(int hid, int axis);
	boolean getHIDButton(int hid, int button);
	int getHIDPOV(int hid, int pov);
}
