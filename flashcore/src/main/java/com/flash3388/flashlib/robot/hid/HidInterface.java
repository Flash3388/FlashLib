package com.flash3388.flashlib.robot.hid;

public interface HidInterface {
	
	boolean isHidConnected(int hid);
	boolean isAxisConnected(int hid, int axis);
	boolean isPovConnected(int hid, int pov);
	boolean isButtonConnected(int hid, int button);
	double getHidAxis(int hid, int axis);
	boolean getHidButton(int hid, int button);
	int getHidPov(int hid, int pov);

	class Stub implements HidInterface {

        @Override
        public boolean isHidConnected(int hid) {
            return false;
        }

        @Override
        public boolean isAxisConnected(int hid, int axis) {
            return false;
        }

        @Override
        public boolean isPovConnected(int hid, int pov) {
            return false;
        }

        @Override
        public boolean isButtonConnected(int hid, int button) {
            return false;
        }

        @Override
        public double getHidAxis(int hid, int axis) {
            return 0;
        }

        @Override
        public boolean getHidButton(int hid, int button) {
            return false;
        }

        @Override
        public int getHidPov(int hid, int pov) {
            return -1;
        }
    }
}
