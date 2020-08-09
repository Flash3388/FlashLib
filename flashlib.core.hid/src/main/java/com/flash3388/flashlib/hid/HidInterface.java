package com.flash3388.flashlib.hid;

public interface HidInterface {

    Axis newAxis(HidChannel channel);
    Button newButton(HidChannel channel);
    Pov newPov(HidChannel channel);

    Hid newGenericHid(HidChannel channel);
    XboxController newXboxController(HidChannel channel);

    class Stub implements HidInterface {

        @Override
        public Axis newAxis(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Button newButton(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Pov newPov(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Hid newGenericHid(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public XboxController newXboxController(HidChannel channel) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
